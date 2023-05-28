package lct.feedbacksrv.resource.generator;

import org.apache.poi.ss.usermodel.Cell;

/**
 * Data cell
 *
 * @author Egor Babenko (e.babenko@lar.tech)
 */
abstract public class DataCell<T> {
    protected T value;

    public DataCell(T value) {
        this.value = value;
    }

    abstract public void writeCell(Cell cell);
}
