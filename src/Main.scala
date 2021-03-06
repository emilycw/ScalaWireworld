/**
 * Created by Emily on 7/10/15.
 */
case class World(cells: List[Cell], yr: Int, xr: Int) {
  def transition(): World = {
    World(transitionCells(cells), yr, xr)
  }

  def transitionCells(cellsList: List[Cell]): List[Cell] = if (cellsList.isEmpty){
    List()
  } else {
    List(transitionOneCell(cellsList.head)) ++ transitionCells(cellsList.tail)
  }

  def transitionOneCell(cell: Cell): Cell = cell.ty match {
    case Head => Cell(cell.xc, cell.yc, Tail)
    case Tail => Cell(cell.xc, cell.yc, Wire)
    case Wire => transitionWire(cell.xc, cell.yc)
    case Empty => Cell(cell.xc, cell.yc, Empty)
  }

  def transitionWire(xc: Int, yc: Int): Cell = if (touching_heads(xc, yc) == 1 || touching_heads(xc, yc) == 2) {
    Cell(xc, yc, Head)
  } else {
    Cell(xc, yc, Wire)
  }

  def touching_heads(xc: Int, yc: Int): Int = cells.map( x => isTouchingIsHeadBinary(xc, yc, x)).sum

  def isTouchingIsHeadBinary(xc: Int, yc: Int, cell: Cell): Int = cell.ty match {
    case Head => cell.isTouchingBinary(xc, yc)
    case _  => 0
  }

  def worldToString(): String = {
    fillInString(blankWorld(0, 0), cells)
  }

  def fillInString(string: String, cellsList: List[Cell]): String = if (cellsList.isEmpty) {
    string
  } else{
    fillInString(addCellToString(string, cellsList.head), cellsList.tail)
  }

  def addCellToString(string: String, cell: Cell): String = {
    val loc = (cell.xc * (xr+1)) + cell.yc
    val start = string.substring(0, loc)
    val end = if (string.length > loc){
      string.substring(loc + 1)
    } else {
      ""
    }
    val character = typeToString(cell)
    start.concat(character).concat(end)
  }

  def typeToString(cell: Cell): String = cell.ty match {
    case Head => "="
    case Wire => "-"
    case Tail => "+"
    case Empty => "_"
  }

  def blankWorld(yp: Int, xp: Int ): String = if (yp >= yr){
    ""
  } else if (xp >= xr) {
    "\n".concat(blankWorld(yp+1, 0))
  } else {
    "_".concat(blankWorld(yp, xp+1))
  }

  def cellsToString(cellsList: List[Cell]) : String = cellsList.head.ty match {
    case Head => "=".concat(cellsToString(cellsList.tail))
    case Tail => "+".concat(cellsToString(cellsList.tail))
    case Wire => "-".concat(cellsToString(cellsList.tail))
    case Empty => "_".concat(cellsToString(cellsList.tail))
  }

  def finished(): Boolean = {
    false
  }
}

sealed abstract class CellType
case object Head extends CellType
case object Tail extends CellType
case object Wire extends CellType
case object Empty extends CellType

case class Cell(xc: Int, yc: Int, ty: CellType) {
  def isTouchingBinary(otherX: Int, otherY: Int): Int = if (otherX - xc >= -1 && otherX - xc <= 1){
    if (otherY - yc >= -1 && otherY - yc <= 1){
      1
    } else {
      0
    }
  } else {
    0
  }
}

object Main {
  def stringToWorld(string: String): World = {
    World(stringToWorldHelper(string, List(), 0, 0), string.count(_ == '\n') +1, string.length/(string.count(_ == '\n') + 1))
  }

  def stringToWorldHelper(string: String, cells: List[Cell], row: Int, col: Int): List[Cell] = if (string.length <= 0){
    List()
//  } else if (string.charAt(0) == '\n') {
//      println("new line")
//      stringToWorldHelper(string.substring(1), cells, row + 1, 0)
  } else if (string.charAt(0) == '='){
      List(Cell(row, col, Head )) ++ stringToWorldHelper(string.substring(1), cells, row , col+1)
  } else if (string.charAt(0) == '-'){
    List(Cell(row, col, Wire)) ++ stringToWorldHelper(string.substring(1), cells, row , col+1)
  } else if (string.charAt(0) == '+'){
    List(Cell(row, col, Tail)) ++ stringToWorldHelper(string.substring(1), cells, row , col+1)
  } else if (string.charAt(0) == '_'){
      stringToWorldHelper(string.substring(1), cells, row , col+1)
  } else {
      stringToWorldHelper(string.substring(1), cells, row + 1, 0)
  }

  def main(args: Array[String]): Unit = {
    displayWorld(stringToWorld("_-+=_\n____-\n____-\n_---_"), 10)
  }

  def displayWorld(world: World, upperLimit: Int): World = if (world.finished() || upperLimit <= 0){
    world
  } else {
    val newWorld = world.transition()
    println(newWorld.worldToString())
    displayWorld(newWorld, upperLimit - 1)
  }
}