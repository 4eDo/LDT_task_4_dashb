package lct.feedbacksrv.resource.generator.DataCellTypes;

import lct.feedbacksrv.resource.generator.DataCell;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Empty cell
 *
 * @author Maria Balashova (m.balashova@lar.tech)
 */
public class EmptyCell extends DataCell<String> {
    public EmptyCell(String value) {
        super(value);
    }

    @Override
    public void writeCell(Cell cell) {
        cell.setCellValue(StringUtils.EMPTY);
    }
}
