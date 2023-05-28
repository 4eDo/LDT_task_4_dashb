package lct.feedbacksrv.resource.generator.DataCellTypes;

import lct.feedbacksrv.resource.generator.DataCell;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Int cell
 *
 * @author Egor Babenko (e.babenko@lar.tech)
 */
public class IntCell extends DataCell<Integer> {
    public IntCell(Integer value) {
        super(value);
    }

    @Override
    public void writeCell(Cell cell) {
        cell.setCellValue(value);
    }
}
