// https://github.com/KalininPavel74/Java_HomeWorkUnit06.git

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.*;
import java.util.ArrayList;
import java.util.Date;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

public class JavaUnit06KalininPavel {

    static Logger logger = Logger.getLogger(JavaUnit06KalininPavel.class.getName());
    static final String LOG_FILE = "log.txt";
    static final String CHARSET_FILE = "UTF-8";
    static final String CHARSET_CONSOLE = "CP866";
    static final String INDENT = "\t\t";

    public static void main(String[] args) {
        // Инициализация логера
        loggerInit(LOG_FILE);

        String taskText = "\nЗадание №1. Реализивать волновой алгоритм.";
        logger.warning(taskText); 

        logger.warning("\nВариант №1   ----два выхода, по одному пути из каждого----\n"); 
        // два выхода, по одному пути из каждого
        case1();
        logger.warning("\n\nВариант №2   ----один выход, два равнозначных пути----\n"); 
        // один выход, два равнозначных пути
        case2();
    }

    static public void case1() {
        DiscreteWorkingField dwf = new DiscreteWorkingField(10, 7);
        logger.warning("Прорисовать забор."); 
        dwf.setFence();
        logger.warning("Прорисовать выходы."); 
        ArrayList<Cell> cellOutputs = new ArrayList<Cell>();
        cellOutputs.add(new Cell(dwf, 8, 0));
        cellOutputs.add(new Cell(dwf, 8, 6));
        dwf.fixOutput(8, 0);
        dwf.fixOutput(8, 6);

        logger.warning("Прорисовать препятствия."); 
        dwf.fixObstacle(2, 2);
        dwf.fixObstacle(3, 2);
        dwf.fixObstacle(4, 2);
        dwf.fixObstacle(5, 2);
        dwf.fixObstacle(4, 3);
        dwf.fixObstacle(3, 4);
        dwf.fixObstacle(4, 4);
        dwf.fixObstacle(5, 4);
        dwf.fixObstacle(6, 4);

        // раскрутить варианты путей до выходов
        UnwindTrace unwindTraceDWF = new UnwindTrace(dwf);
        // указать начальную ячейку
        Cell beginCell = unwindTraceDWF.setBeginCell(2, 3);
        if(beginCell == null) {
            logger.severe("Не удалось назначить начальную ячейку.");
            System.exit(0);
        }
        // отобразить начальную карту
        //logger.warning(dwf.print(DiscreteWorkingField.EMPTY));
        logger.warning(dwf.toString());

        logger.warning("Прорисовать все ходы."); 
        unwindTraceDWF.unwindTrace();
        // отобразить карту с раскрученными путями без забора
//        logger.warning(dwf.print(DiscreteWorkingField.FENCE));
        logger.warning(dwf.toString());
        //logger.warning(unwindTraceDWF.toString());

        // поиск кратчаших путей от выходов к начальной ячейке
        FindWays findWays = new FindWays(dwf, cellOutputs, beginCell);
        // отобразить карту с кратчайшими путями
        logger.warning(dwf.toString(findWays.ways));
        // координаты кратчайших путей
        logger.warning(findWays.print());
    }

    static public void case2() {
        DiscreteWorkingField dwf = new DiscreteWorkingField(10, 5);
        logger.warning("Прорисовать забор."); 
        dwf.setFence();
        logger.warning("Прорисовать выходы."); 
        ArrayList<Cell> cellOutputs = new ArrayList<Cell>();
        cellOutputs.add(new Cell(dwf, 9, 2));
        dwf.fixOutput(9, 2);

        logger.warning("Прорисовать препятствия."); 
        dwf.fixObstacle(2, 2);
        dwf.fixObstacle(3, 2);
        dwf.fixObstacle(4, 2);
        dwf.fixObstacle(5, 2);
        dwf.fixObstacle(6, 2);
        dwf.fixObstacle(7, 2);

        // раскрутить варианты путей до выходов
        UnwindTrace unwindTraceDWF = new UnwindTrace(dwf);
        // указать начальную ячейку
        Cell beginCell = unwindTraceDWF.setBeginCell(1, 2);
        if(beginCell == null) {
            logger.severe("Не удалось назначить начальную ячейку.");
            System.exit(0);
        }
        // отобразить начальную карту
        logger.warning(dwf.toString());

        logger.warning("Прорисовать все ходы."); 
        unwindTraceDWF.unwindTrace();
        // отобразить карту с раскрученными путями без забора
        logger.warning(dwf.toString());
        //logger.warning(unwindTraceDWF.toString());

        // поиск кратчаших путей от выходов к начальной ячейке
        FindWays findWays = new FindWays(dwf, cellOutputs, beginCell);
        // отобразить карту с кратчайшими путями
        logger.warning(dwf.toString(findWays.ways));
        // координаты кратчайших путей
        logger.warning(findWays.print());

    }

    static class FindWays {

        ArrayList<ArrayList<Cell>> ways = new ArrayList<ArrayList<Cell>>();

        DiscreteWorkingField dwf = null;
        List<Cell> cellOutputs = null;
        Cell beginCell = null;

        Queue<ArrayList<Cell>> queue = null;

        public FindWays(DiscreteWorkingField dwf, List<Cell> cellOutputs, Cell beginCell) {
            this.dwf = dwf;
            this.cellOutputs = cellOutputs;
            this.beginCell = beginCell;
            queue = new LinkedList<ArrayList<Cell>>();

            for(Cell cellOutput: cellOutputs) {
                logger.info("Поиск путей до выхода " + cellOutput.print()); 
                ArrayList<Cell> way = createWay(null, cellOutput);
//                ArrayList<Cell> currWay = new ArrayList<Cell>();
//                queue.add(currWay); 
//                ways.add(currWay);
//                currWay.add(cellOutput);

                // найти ячейку перед выходом - одна одна
                Cell nextCell = cellOutput.getUp();
                if(nextCell == null || !nextCell.isfixTrace()) {
                    nextCell = cellOutput.getRight();
                    if(nextCell == null || !nextCell.isfixTrace()) {
                        nextCell = cellOutput.getDown();
                        if(nextCell == null || !nextCell.isfixTrace()) {
                            nextCell = cellOutput.getLeft();
                        }    
                    }    
                }
                logger.info("Последняя ячейка перед выходом "+nextCell.print());                 
                way.add(nextCell);
            }
            findWays();   
        }

        private ArrayList<Cell> createWay(ArrayList<Cell> beginWay, Cell addCell) {
            //logger.warning("Новый путь от "+beginWay+". Новая ячейка "+addCell.print()); 
            ArrayList<Cell> way = new ArrayList<Cell>();
            if(beginWay != null)
                for(Cell c: beginWay) 
                    way.add(c);
            way.add(addCell);        
            queue.add(way); 
            ways.add(way);
            return way;
        }

        private void findWays() {
            while(queue.size()>0) {
                ArrayList<Cell> way = queue.poll();
                // восстановить путь до статовой ячейки
                // поиск значения на 1 миньше, чем в текущей ячейки
                Cell beforeCell = way.get(way.size()-1);
                while(true) {
                    logger.info("до minValueCellFromCross "+beforeCell.print()); 
                    ArrayList<Cell> nextCells = nextCellsFromCross(beforeCell);
                    if(nextCells == null || nextCells.size() == 0) break;
                    beforeCell = nextCells.get(0);
                    // если несколько альтернативных путей из одной ячейки
                    for(Cell c: nextCells) {
                        if(!c.equals(beforeCell)) // не текущий путь
                            createWay(way, c); // создать самостоятельные пути и добавить в очередь
                    }
                    way.add(beforeCell);
                }    
            }    
        }

        private ArrayList<Cell> nextCellsFromCross(Cell beforeCell) {
            if(beforeCell == null || !beforeCell.isfixTrace()) return null; // на всякий случай
            ArrayList<Cell> resultCells = new ArrayList<Cell>();
            int beforeCellValue = beforeCell.getValue();
            //Cell minValueCell = null;
            Cell currCell = null;
            // Up cell
            currCell = beforeCell.getUp();
            if(currCell != null && currCell.isfixTrace() && currCell.getValue() == beforeCellValue-1) resultCells.add(currCell);
            // Right cell
            currCell = beforeCell.getRight();
            if(currCell != null && currCell.isfixTrace() && currCell.getValue() == beforeCellValue-1) resultCells.add(currCell);
            // Down cell
            currCell = beforeCell.getDown();
            if(currCell != null && currCell.isfixTrace() && currCell.getValue() == beforeCellValue-1) resultCells.add(currCell);
            // Left cell
            currCell = beforeCell.getLeft();
            if(currCell != null && currCell.isfixTrace() && currCell.getValue() == beforeCellValue-1) resultCells.add(currCell);

            return resultCells;
        }

        public String print() {
            StringBuilder sb = new StringBuilder();
            for(ArrayList<Cell> way: ways) {
                Cell outputCell = way.get(0);
                String s  = String.format(" width=%d high=%d ",outputCell.width,outputCell.high);
                String s2 = String.format("Кол-во шагов до выхода (%s): %d",s, way.size());
                sb.append(s2).append("\n");
                for(Cell cell: way) {
                    s = String.format(" width=%d high=%d (value=%d) ",cell.width,cell.high,cell.getValue());
                    sb.append(s).append("\n");
                }    
            }
            return sb.toString();        
        }
    }


    // Класс для раскручивания вариантов трасс (путей)
    static class UnwindTrace {

        Queue<Cell> queue = null; // очередь для хранения конков, по которым нужно раскрутить путь
        DiscreteWorkingField dwf = null; // карта
        Cell beginCell = null; // начальная ячейка

        public UnwindTrace(DiscreteWorkingField dwf) {
            this.dwf = dwf;
            queue = new LinkedList<Cell>();
        }

        // определение ячейки для начала движения
        public Cell setBeginCell(int width, int high) {
            if(beginCell == null) {
                Cell cell = new Cell(dwf, width, high);
                if(cell.isEmpty()) { // если свободная
                    cell.fixTrace(cell); 
                    queue.offer(cell); 
                    beginCell = cell;
                }
            }    
            return beginCell;
        }

        // раскручивание маршрутов 
        public void unwindTrace() {
            while(queue.size()>0) {
                Cell cell = queue.poll();
                logger.info("Прорисовать ячейки крестом от ячейки " + cell.print()); 
                markCell(cell, cell.getUp());
                markCell(cell, cell.getRight());
                markCell(cell, cell.getDown());
                markCell(cell, cell.getLeft());
                logger.info(toString());
            }    
        }

        private void markCell(Cell fromCell, Cell toCell) {
            if(toCell != null && toCell.isEmpty()) { 
                toCell.fixTrace(fromCell); 
                queue.offer(toCell); 
            }
        }

//        public int size() {
//            return queue.size();
//        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("q size=").append(queue.size()).append("\n");
            for(Cell cell: queue)
                sb.append(cell.print()).append("\n");
            return sb.toString();
        }
    }

    // Класс ячеки карты - для удобства, чтобы кода было меньше,
    // и код был "верхнего уроня", более абстракным
    // главное - своего типа и соседей
    static class Cell {
        
        int width;
        int high;
        int key;
        DiscreteWorkingField dwf = null; // ячейка знает свою карту

        public Cell(DiscreteWorkingField dwf, int width, int high) {
            this.width = width;
            this.high = high;
            this.key = dwf.getKey(width, high);
            this.dwf = dwf;
        }

        public int getValue() {
            return dwf.getValue(key);
        }

        public boolean isObstacle() {
            return getValue() == DiscreteWorkingField.OBSTACLE;
        }

        public boolean isEmpty() {
            return getValue() == DiscreteWorkingField.EMPTY;
        }

        public boolean isOutput() {
            return getValue() == DiscreteWorkingField.OUTPUT;
        }

        public boolean isfixTrace() {
            return getValue() > DiscreteWorkingField.EMPTY;
        }

        public void fixTrace(Cell fromCell) {
            dwf.fixTrace(fromCell, key);
        }

        public Cell getUp() {
            if(high == 0) return null;
            return new Cell(dwf, width, high-1);
        }

        public Cell getDown() {
            if(high == dwf.highMap-1) return null;
            return new Cell(dwf, width, high+1);
        }

        public Cell getLeft() {
            if(width == 0) return null;
            return new Cell(dwf, width-1, high);
        }

        public Cell getRight() {
            if(width == dwf.widthMap-1) return null;
            return new Cell(dwf, width+1, high);
        }

        public String print(){
            return String.format("cell width=%d high=%d key=%d value=%d",width,high,key, getValue());
        }
    }

    // Класс для хранения, заполнения и печати карты (Дискретное рабочее поле)
    static class DiscreteWorkingField {
        
        int widthMap; // количество ячеек карты в ширину
        int highMap;  // количество ячеек карты в высоту
        // карта хранится в массиве - ячейка карты (width и high) соответствуют ячейке массива
        // key = width + this.widthMap * high
        int[] ar = null; 
        static final int EMPTY = 0;  // пустая ячейка
        static final int OUTPUT = -2;  // выход
        static final int OBSTACLE = -1; // внутреннее препятствие
        static final int FENCE = -9; // забор

        static final String STR_EMPTY    = " \u2591\u2591\u2591";
        static final String STR_OUTPUT   = "\u2592\u2592\u2592\u2592";
        static final String STR_OBSTACLE = " \u25A0\u25A0\u25A0";
        static final String STR_FENCE    = "\u2588\u2588\u2588\u2588";

        public DiscreteWorkingField(int width, int high) {
            this.widthMap = width;
            this.highMap = high;
            int len = width * high;
            ar = new int[len];
            for(int i=0; i<len; i++)
                ar[i] = 0;
        }

        public int getKey(int width, int high) {
            return width + this.widthMap * high;
        }

        public int getValue(int width, int high) {
            return ar[getKey(width, high)];
        }

        public int getValue(int key) {
            return ar[key];
        }

        public void fixTrace(Cell fromCell, int key) {
            if(ar[key] == EMPTY)
                ar[key] = fromCell.getValue() + 1;
        }

        public void fixOutput(int width, int high) {
            ar[getKey(width, high)] = OUTPUT;
        }

        public void fixObstacle(int key) {
            if(ar[key] == EMPTY)
                ar[key] = OBSTACLE;
        }

        public void fixObstacle(int width, int high) {
            fixObstacle(getKey(width, high));
        }

        public void setFence() {
            for(int i=0; i<widthMap; i++) {
                fixFence(i);
                fixFence(i + widthMap*(highMap-1));
            } 
            for(int i=0; i<highMap; i++) {
                fixFence(widthMap*i);
                fixFence(widthMap*i + widthMap-1);
            }    
        }

        private void fixFence(int key) {
            if(ar[key] == EMPTY)
                ar[key] = FENCE;
        }
/*
        public int getWidth(int key) {
            return key - width * (key/width);
        }

        public int getHigh(int key) {
            return key/width;
        }
*/        

        public String toЕxpandString(String aS) {
            return toЕxpandString(aS, 4);
        }

        public String toЕxpandString(String aS, int aWight) {
            int d = aWight - aS.length();
            if(d>0) {
                if(d==1) return " "   +aS;                
                if(d==2) return "  "  +aS;                
                if(d==3) return "   " +aS;                
                if(d==4) return "    "+aS;                
                StringBuilder sb = new StringBuilder();
                for(int i=0; i<d; i++) sb.append(" ");
                sb.append(aS);
                return sb.toString();                
            } else return aS;
        }

        // Печать карты
        public String toString() {
            return toString(null);
        }    
        
        public String toString(ArrayList<ArrayList<Cell>> ways) {
            int len = ar.length;
            StringBuilder sb = new StringBuilder(len*3);

            String lineSeparator = System.getProperty("line.separator");
            sb.append(lineSeparator).append("   ");
            for(int i=0; i<widthMap; i++) 
                sb.append(toЕxpandString(""+i));
            sb.append(lineSeparator);
            sb.append("  |");
            for(int i=0; i<widthMap; i++) 
                sb.append("____");
            sb.append(" width");
            sb.append(lineSeparator).append(lineSeparator);

            for(int i=0; i<highMap; i++) {
                sb.append(i).append(" |");
                for(int j=0; j<widthMap; j++) {
                    int v = getValue(j, i);
                    if(v == FENCE)
                        sb.append(STR_FENCE);
                    else if(v == OUTPUT)
                        sb.append(STR_OUTPUT);
                    else if(v == EMPTY)
                        sb.append(STR_EMPTY);
                    else if(v == OBSTACLE)
                        sb.append(STR_OBSTACLE);
                    else {
                        String s_way = "";
                        if(ways != null) {
                            for(ArrayList<Cell> way: ways)
                                for(Cell c: way)
                                    if(c.width == j && c.high == i)
                                        s_way += "+";
                        }                
                        sb.append(toЕxpandString(s_way+v));
                    }    
                }    
                sb.append(lineSeparator);
            }
            sb.append("high");
            return sb.toString();
        }
    }

    // Инициализация логера
    public static void loggerInit(String aFileName) {
        FileHandler fh = null;
        try {
             fh = new FileHandler(aFileName, true);
        } catch (Exception e) {
            System.out.println("Проблемы с файлом "+aFileName+" "+e.getMessage());
        } 
        if(fh == null) System.exit(0);     
        try {
            fh.setEncoding(CHARSET_FILE);
        } catch (Exception e) {
           System.out.println("Проблемы с кодировкой FileHandler "+e.getMessage());
        } 
        fh.setLevel(Level.INFO); // все что ниже INFO не работает, зараза.
        //fh.setLevel(Level.FINE);
        logger.addHandler(fh);
      
//        SimpleFormatter sFormat = new SimpleFormatter();
//        fh.setFormatter(sFormat);
        fh.setFormatter(withoutRipplesInTheEyesFormatter);

        // Изменение консольного логера, которые уже создан по умолчанию
        for (Handler h : LogManager.getLogManager().getLogger("").getHandlers()) {
            if (h instanceof ConsoleHandler) {
                if (h.getFormatter() == null || !(h.getFormatter() instanceof EmptyFormatter)) {
                        h.setFormatter(emptyFormatter);
                    try {
                        h.setEncoding(CHARSET_CONSOLE);
                        h.setLevel(Level.WARNING); // все что ниже INFO не работает, зараза.
                        //h.setLevel(Level.INFO);
                    } catch (Exception e) {
                       System.out.println("Проблемы с кодировкой ConsoleHandler "+e.getMessage());
                    } 
                    //break; 
                }
            }
        }         
/*      // он там по умолчанию создан   
        ConsoleHandler ch = new ConsoleHandler();
        ch.setFormatter(sFormat);
        try {
            ch.setEncoding(CHARSET_CONSOLE);
        } catch (Exception e) {
           System.out.println("Проблемы с кодировкой ConsoleHandler "+e.getMessage());
        } 
        logger.addHandler(ch);
*/
        logger.info(INDENT+"\n\n------------------------------------------------------------\n");
        logger.info(INDENT+"Логирование инициализировано");
    }

    // Создание пустого формата для консоли
    static class EmptyFormatter extends Formatter {
        String lineSeparator = System.getProperty("line.separator");
        @Override public synchronized String format(LogRecord record) {
            return formatMessage(record)+lineSeparator;
        }
    }
    static EmptyFormatter emptyFormatter = new EmptyFormatter();

    static final Date dat = new Date();

    // Создание формата для файла лога, чтобы не рябило в глазах.
    static class WithoutRipplesInTheEyesFormatter extends Formatter {
        String lineSeparator = System.getProperty("line.separator");
        String yyyy_MM_dd_HH_mm_ss = "yyyy.MM.dd HH:mm:ss";
        @Override public synchronized String format(LogRecord record) {
            dat.setTime(record.getMillis());
            String dateStr = (new SimpleDateFormat( yyyy_MM_dd_HH_mm_ss )).format(dat);
            String message = formatMessage(record);
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                pw.print("");
                record.getThrown().printStackTrace(pw);
                pw.close();
                throwable = sw.toString();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(dateStr)
                .append(" ")
                .append(((record.getSourceClassName() != null) ? record.getSourceClassName() : record.getLoggerName()))
                .append(" ")
                .append(((record.getSourceMethodName() != null) ? record.getSourceMethodName() : ""))
                .append("\t")
                .append(record.getLevel().getName())
                .append("  ")
                .append(message)
                .append(throwable)
                .append(lineSeparator);
            return sb.toString();
        }
    }
    static WithoutRipplesInTheEyesFormatter withoutRipplesInTheEyesFormatter 
        = new WithoutRipplesInTheEyesFormatter();


}