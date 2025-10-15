package com.example.spendless.features.finance.presentation.ui.screens.transactions

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendless.R
import com.example.spendless.core.data.constant.Constants
import com.example.spendless.core.data.model.Category
import com.example.spendless.core.domain.auth.SessionStorage
import com.example.spendless.core.domain.util.Result
import com.example.spendless.core.presentation.ui.amountFormatter
import com.example.spendless.features.auth.domain.UserRepository
import com.example.spendless.features.finance.data.model.ExportFormat
import com.example.spendless.features.finance.data.model.ExportRange
import com.example.spendless.features.finance.data.model.PaymentRecurrence
import com.example.spendless.features.finance.data.model.TransactionItem
import com.example.spendless.features.finance.domain.SessionExpiryUseCase
import com.example.spendless.features.finance.domain.TransactionsRepository
import com.example.spendless.features.finance.presentation.ui.common.SharedActions
import com.example.spendless.features.finance.presentation.ui.common.groupTransactionsByDate
import com.example.spendless.features.finance.presentation.ui.screens.transactions.util.FileExporter
import com.example.spendless.features.finance.presentation.ui.screens.transactions.util.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import timber.log.Timber
import javax.inject.Inject

sealed interface TransactionsEvents {
    data object NavigateBack : TransactionsEvents
    data object PromptPin : TransactionsEvents
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val saveStateHandle: SavedStateHandle,
    private val sessionStorage: SessionStorage,
    private val transactionsRepository: TransactionsRepository,
    private val userRepository: UserRepository,
    private val fileExporter: FileExporter,
    private val sessionExpiryUseCase: SessionExpiryUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(TransactionsUiState())
    val state = _state.asStateFlow()

    private val _events = Channel<TransactionsEvents>()
    val events = _events.receiveAsFlow()

    private var exportJob: Job? = null

    init {
        viewModelScope.launch {
            initialSetUp()
            setPreferencesFormat()
            getAllTransactions()
        }
    }

    private suspend fun initialSetUp() {
        val showExportRange = saveStateHandle.get<Boolean>("showExportRange") == true
        val username = sessionStorage.getAuthInfo()!!.username
        _state.update { newState ->
            newState.copy(
                showExportBottomSheet = showExportRange,
                username = username
            )
        }
    }

    fun onActions(transactionsActions: SharedActions) {
        when (transactionsActions) {
            SharedActions.TransactionsActions.ExportData -> exportData()
            SharedActions.TransactionsActions.NavigateBack -> navigateBack()
            SharedActions.DismissBottomSheet -> dismissBottomSheet()
            SharedActions.ShowBottomBar -> showBottomBar()
            SharedActions.OnCreateClick -> onCreateClick()

            is SharedActions.SelectedTransaction -> setSelectedTransaction(transactionsActions.selectedTransactionItem)
            is SharedActions.UpdateExpense -> updateExpense(transactionsActions.isExpense)
            is SharedActions.UpdateTextFieldValue -> updateTextFieldValue(transactionsActions.textFieldValue)
            is SharedActions.UpdateAmountTextFieldValue -> updateAmountTextFieldValue(
                transactionsActions.amountTextFieldValue
            )

            is SharedActions.UpdateNote -> updateNote(note = transactionsActions.noteValue)
            is SharedActions.UpdateSelectedCategory -> updateSelectedCategory(
                transactionsActions.category
            )

            is SharedActions.UpdateDropDownCategoryExpand -> updateDropDownCategoryExpand(
                transactionsActions.isExpand
            )

            is SharedActions.UpdateSelectedPaymentRecurrence -> updateSelectedPaymentRecurrence(
                transactionsActions.paymentRecurrence
            )

            is SharedActions.UpdateDropDownPaymentRecurrenceExpand -> updateDropDownPaymentRecurrenceExpand(
                transactionsActions.isExpand
            )

            is SharedActions.ShowFloatingActionButton -> showFloatingActionButton(
                transactionsActions.isVisible
            )

            SharedActions.TransactionsActions.DismissExportBottomSheet -> dismissExportBottomSheet()
            SharedActions.TransactionsActions.ShowExportBottomBar -> showExportBottomBar()
            is SharedActions.TransactionsActions.UpdateSelectedExportRange -> updateSelectedExportRange(
                transactionsActions.exportRange
            )

            is SharedActions.TransactionsActions.UpdateDropDownExportRangeExpand -> updateDropDownExportRangeExpand(
                transactionsActions.isExpand
            )

            is SharedActions.TransactionsActions.UpdateSelectedExportFormat -> updateSelectedExportFormat(
                transactionsActions.exportFormat
            )

            is SharedActions.TransactionsActions.UpdateDropDownExportFormatExpand -> updateDropDownExportFormatExpand(
                transactionsActions.isExpand
            )

            SharedActions.TransactionsActions.ShowExportRangeList -> showExportRangeList()

            else -> {}
        }
    }

    private fun showExportRangeList() {
        _state.update { newState ->
            newState.copy(
                exportRangeList = Constants.exportRangeList,
                selectedExportRange = Constants.exportRangeList.first()
            )
        }
    }

    private suspend fun setPreferencesFormat() {
        val username = _state.value.username
        val result = userRepository.getPreferencesByUsername(username)
        if (result is Result.Success) {
            _state.update { newState ->
                newState.copy(
                    bottomSheetUiState = newState.bottomSheetUiState.copy(
                        preferencesFormat = result.data,
                    )
                )
            }
            Timber.tag("MyTag").d("preferences: ${result.data}")
        }
    }

    private suspend fun getAllTransactions() {
        val transactionsList = transactionsRepository.getAllTransactions()
        transactionsList.collect { transactionsList ->
            val transactionMap = transactionsList.map {
                it.copy(
                    price = amountFormatter(
                        total = it.price,
                        isExpense = it.isExpense,
                        preferencesFormat = _state.value.bottomSheetUiState.preferencesFormat
                    )
                )
            }
            //map the date with their lists for example 9/10/2024 should have a list with key 9/10/2024 and value
            //all the transactionItems at this date
            val groupTransactions = groupTransactionsByDate(transactions = transactionMap)
            _state.update { newState ->
                newState.copy(
                    bottomSheetUiState = newState.bottomSheetUiState.copy(listOfTransactions = transactionsList),
                    transactionsByDate = groupTransactions,
                    isLoading = false
                )
            }
        }
    }

    private fun showFloatingActionButton(isVisible: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    isFloatingActionButtonVisible = isVisible
                )
            )
        }
    }

    private fun onCreateClick() {
        viewModelScope.launch {
            //show loader for on create button
            onCreateLoading(true)
            val timeNow = Clock.System
                .now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            val state = _state.value
            val transactionItem = state.bottomSheetUiState.selectedTransactionItem.copy(
                category = state.bottomSheetUiState.selectedCategory,
                title = state.bottomSheetUiState.textFieldValue,
                isExpense = state.bottomSheetUiState.isExpense,
                description = if (state.bottomSheetUiState.isExpense) state.bottomSheetUiState.selectedCategory.categoryName.categoryRes else R.string.income,
                price = state.bottomSheetUiState.amountTextFieldValue.text,
                date = timeNow.toString(),
                content = state.bottomSheetUiState.noteValue,
                image = if (state.bottomSheetUiState.isExpense) state.bottomSheetUiState.selectedCategory.image else R.drawable.accessories
            )
            Timber.tag("MyTag").d("onCreateClick $transactionItem")
            val result = transactionsRepository.insertTransaction(transactionItem)
            when (result) {
                is Result.Error -> Timber.tag("MyTag").d("onCreateClick error: ${result.error}")
                is Result.Success -> {
                    _state.update { newState ->
                        newState.copy(
                            bottomSheetUiState = newState.bottomSheetUiState.copy(showBottomSheet = false)
                        )
                    }
                    resetTextFields()
                    onCreateLoading(false)
                    dismissBottomSheet()
                }
            }
        }
    }

    private fun onCreateLoading(onCreateLoading: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    isOnCreateLoading = onCreateLoading
                )
            )
        }
    }

    private fun updateDropDownPaymentRecurrenceExpand(isExpand: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    isDropDownPaymentRecurrenceExpand = isExpand
                )
            )
        }
    }

    private fun updateSelectedPaymentRecurrence(paymentRecurrence: PaymentRecurrence) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(selectedPaymentRecurrence = paymentRecurrence)
            )
        }
    }

    private fun updateDropDownCategoryExpand(isExpand: Boolean) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(isDropDownCategoryExpand = isExpand)
            )
        }
    }

    private fun updateSelectedCategory(selectedCategory: Category) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(selectedCategory = selectedCategory)
            )
        }
    }

    private fun updateNote(note: String) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(noteValue = note)
            )
        }
    }

    private fun updateAmountTextFieldValue(amountTextFieldValue: TextFieldValue) {
        if (!(amountTextFieldValue.text.startsWith("0"))) {
            //to not use the format and only digits and only 8 length
            val digitsOnly = amountTextFieldValue.text.filter { it.isDigit() }.take(8)
            //set textField cursor to the end
            val textFieldValue =
                TextFieldValue(text = digitsOnly, selection = TextRange(digitsOnly.length))
            _state.update { newState ->
                newState.copy(
                    bottomSheetUiState = newState.bottomSheetUiState.copy(
                        amountTextFieldValue = textFieldValue
                    )
                )
            }
        }
    }

    private fun updateTextFieldValue(textFieldValue: String) {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    textFieldValue = textFieldValue,
                )
            )
        }
    }

    private fun showExportBottomBar() {
        _state.update { newState ->
            newState.copy(
                showExportBottomSheet = true
            )
        }
    }

    private fun updateSelectedExportRange(exportRange: ExportRange) {
        if (exportRange !is ExportRange.SpecificMonth) {
            _state.update { newState ->
                newState.copy(
                    selectedExportRange = exportRange
                )
            }
        } else {
            val getPast12Months = Utils.getPast12Months()
            _state.update { newState ->
                newState.copy(
                    selectedExportRange = exportRange,
                    exportRangeList = getPast12Months
                )
            }
        }
    }

    private fun updateDropDownExportRangeExpand(isExpand: Boolean) {
        _state.update { newState ->
            newState.copy(isExportRangeExpand = isExpand)
        }
    }

    private fun updateSelectedExportFormat(exportFormat: ExportFormat) {
        _state.update { newState ->
            newState.copy(
                selectedExportFormat = exportFormat
            )
        }
    }

    private fun updateDropDownExportFormatExpand(isExpand: Boolean) {
        _state.update { newState ->
            newState.copy(isExportFormatExpand = isExpand)
        }
    }

    private fun dismissExportBottomSheet() {
        _state.update { newState ->
            newState.copy(showExportBottomSheet = false)
        }
    }

    private fun showBottomBar() {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(showBottomSheet = true)
            )
        }
    }

    private fun dismissBottomSheet() {
        _state.update { newState ->
            newState.copy(bottomSheetUiState = newState.bottomSheetUiState.copy(showBottomSheet = false))
        }
    }

    private fun updateExpense(isExpense: Boolean) {
        _state.update { newState ->
            newState.copy(bottomSheetUiState = newState.bottomSheetUiState.copy(isExpense = isExpense))
        }
    }

    private fun setSelectedTransaction(selectedTransaction: TransactionItem) {
        val isSelected =
            selectedTransaction == _state.value.bottomSheetUiState.selectedTransactionItem
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(selectedTransactionItem = if (isSelected) TransactionItem() else selectedTransaction)
            )
        }
    }


    private fun navigateBack() {
        viewModelScope.launch {
            _events.send(TransactionsEvents.NavigateBack)
        }
    }

    private fun resetTextFields() {
        _state.update { newState ->
            newState.copy(
                bottomSheetUiState = newState.bottomSheetUiState.copy(
                    textFieldValue = "",
                    amountTextFieldValue = TextFieldValue(""),
                    noteValue = null
                )
            )
        }
    }


    private fun exportData() {
        exportJob?.cancel()
        exportJob = viewModelScope.launch {
            val isExpiry = isExpiry()
            if (isExpiry) {
                promptPin()
                return@launch
            }
            val exportRange = _state.value.selectedExportRange
            val exportFormat = _state.value.selectedExportFormat
            try {
                _state.update { newState ->
                    newState.copy(isExportButtonLoading = true)
                }
                when (exportRange) {
                    ExportRange.AllData -> exportDataForAllData()
                    ExportRange.CurrentMonth -> exportDataForCurrentMonth()
                    ExportRange.LastMonth -> exportDataForLastMonth()
                    ExportRange.LastThreeMonths -> exportDataForLastThreeMonths()
                    is ExportRange.SpecificMonth -> exportDataForSpecificMonth()
                }
                when (exportFormat) {
                    ExportFormat.CSV -> {
                        exportFormatCSV()
                    }

                    ExportFormat.PDF -> {
                        exportFormatPDF()
                    }
                }
            } catch (e: Exception) {
                Timber.tag("MyTag").e("$e: ${e.localizedMessage}")
                _state.update { newState ->
                    newState.copy(
                        isExportButtonLoading = false,
                        transactionsToExport = null
                    )
                }
            } finally {
                Timber.tag("MyTag").d("exportData completed!")
                _state.update { newState ->
                    newState.copy(
                        isExportButtonLoading = false,
                    )
                }
            }
        }
    }


    private fun exportFormatCSV() {
        val username = _state.value.username
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val safeFileName = "${username}_${today}"
        val csvData = buildString {
            appendLine("Title,Category,Price,Expense,Date")
            _state.value.formattedTransactionToExport?.forEach {
                appendLine(it.toCsv())
            }
        }
        fileExporter.saveCsvToDownloads(fileName = safeFileName, csvData = csvData)
    }

    private fun exportFormatPDF() {
        val username = _state.value.username
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val safeFileName = "${username}_${today}"
        fileExporter.savePdfToDownloads(
            fileName = safeFileName,
            transactionList = _state.value.formattedTransactionToExport
                ?: emptyList<TransactionItem>()
        )
    }

    private suspend fun exportDataForAllData() {
        val transactionList = transactionsRepository.getAllTransactionsForAllData()
        Timber.tag("MyTag").d("allData: ${transactionList.size}")
        _state.update { newState ->
            newState.copy(transactionsToExport = transactionList)
        }
    }

    private suspend fun exportDataForCurrentMonth() {
        val transactionList = transactionsRepository.getTransactionsCurrentMonth()
        Timber.tag("MyTag").d("getTransactionsCurrentMonth: ${transactionList.size}")
        _state.update { newState ->
            newState.copy(transactionsToExport = transactionList)
        }
    }

    private suspend fun exportDataForLastMonth() {
        val transactionList = transactionsRepository.getTransactionsLastMonth()
        Timber.tag("MyTag").d("getTransactionsCurrentMonth: ${transactionList.size}")
        _state.update { newState ->
            newState.copy(transactionsToExport = transactionList)
        }
    }

    private suspend fun exportDataForLastThreeMonths() {
        val transactionList = transactionsRepository.getTransactionsLastThreeMonths()
        Timber.tag("MyTag").d("getTransactionsLastThreeMonths: ${transactionList.size}")
        _state.update { newState ->
            newState.copy(transactionsToExport = transactionList)
        }
    }

    private suspend fun exportDataForSpecificMonth() {
        val selectedExportRange = _state.value.selectedExportRange as ExportRange.SpecificMonth
        val specificMonth = selectedExportRange.monthNumber
        val specificYear = selectedExportRange.year
        val transactionList = transactionsRepository.getTransactionsSpecificMonth(
            specificMonth = specificMonth,
            specificYear = specificYear
        )
        Timber.tag("MyTag").d("exportDataForSpecificMonth: ${transactionList.size}")
        _state.update { newState ->
            newState.copy(transactionsToExport = transactionList)
        }
    }

    private fun promptPin() {
        viewModelScope.launch {
            _events.send(TransactionsEvents.PromptPin)
        }
    }

    private suspend fun isExpiry(): Boolean{
            val isExpired = sessionExpiryUseCase.invoke()
            return isExpired
    }
}