package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    var selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()
    val isTakingNotesLiveData = MutableLiveData<Boolean>()
    val highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    val showCongratulationData = MutableLiveData<Boolean>()

    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false

    private val board: Board
    private val data: Data = Data()

    init {
        showCongratulationData.postValue(false)
        val cells = List(9 * 9) {i -> Cell(i / 9, i % 9, data.startData[i / 9][i % 9],
            data.startData[i / 9][i % 9] != 0)}
        board = Board(9, cells)
        isTakingNotesLiveData.postValue(isTakingNotes)

        selectedCellLiveData.postValue(Pair(selectedRow, selectedCol))
        cellsLiveData.postValue(board.cells)
    }

    fun handleInput(number: Int) {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return

        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            highlightedKeysLiveData.postValue(cell.notes)
        } else {
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
        var completed = true;
        var i = 0;
        for (cell in board.cells){
            if (cell.value != data.answer[i / 9][i % 9]){
                completed = false;
                break;
            }
            i++;
        }
        if (completed)
            showCongratulation()
    }

    fun showCongratulation(){
        for (cell in board.cells){
            cell.value = 0
            cell.notes.clear()
        };
        showCongratulationData.postValue(true)
    }

    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if(!cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }

    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if (isTakingNotes) {
            board.getCell(selectedRow, selectedCol).notes
        } else {
            setOf<Int>()
        }
        highlightedKeysLiveData.postValue(curNotes)
    }

    fun delete() {
        val cell = board.getCell(selectedRow, selectedCol)
        if(isTakingNotes) {
            cell.notes.clear()
            highlightedKeysLiveData.postValue(setOf())
        } else {
            cell.value = 0
        }
        cellsLiveData.postValue(board.cells)
    }
}