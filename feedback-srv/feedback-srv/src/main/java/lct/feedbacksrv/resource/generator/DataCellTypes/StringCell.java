package lct.feedbacksrv.resource.generator.DataCellTypes;

import lct.feedbacksrv.resource.generator.DataCell;
import org.apache.poi.ss.usermodel.Cell;

/**
 * String cell
 *
 * @author Egor Babenko (e.babenko@lar.tech)
 */
public class StringCell extends DataCell<String> {
    public StringCell(String value) {
        super(value);
    }

    @Override
    public void writeCell(Cell cell) {
        cell.setCellValue(value);
    }
}
