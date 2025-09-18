package mk.finki.ukim.proekt_ib;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
public class Pixel {
    private int x;
    private int y;
    private Color color;

    public Pixel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }


}
