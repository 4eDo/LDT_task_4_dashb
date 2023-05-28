package lct.feedbacksrv.resource.csv;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Charsets {

    WINDOWS_1251("windows-1251"),
    WINDOWS_1252("windows-1252");

    private String name;
}
