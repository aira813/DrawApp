package jp.tkcdroid.drawapp.view.dto;

import android.graphics.Paint;
import android.graphics.Path;

public class PathDto {

    private Path path;
    private Paint paint;

    public PathDto(Path path, Paint paint) {
        this.path = path;
        this.paint = paint;
    }

    public Path getPath() {
        return path;
    }

    public Paint getPaint() {
        return paint;
    }
}
