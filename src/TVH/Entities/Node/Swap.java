package TVH.Entities.Node;

import TVH.Entities.Stop;

import java.util.Objects;

public class Swap {
    public int i1;
    public int i2;

    public Swap(int i1, int i2) {
        this.i1 = i1;
        this.i2 = i2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swap)) return false;
        Swap that = (Swap) o;
        if(i1 == that.i1 || i1 == that.i2){
            if(i2 == that.i1 || i2 == that.i2){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i1)+Objects.hash(i2);
    }
}
