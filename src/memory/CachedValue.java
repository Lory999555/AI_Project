package memory;

import java.util.Objects;

public class CachedValue {
  private final Flag flag;
  private final int value;
  private final int depth;

  public CachedValue(Flag flag, int value, int depth) {
    this.flag = flag;
    this.value = value;
    this.depth = depth;
  }

  public Flag getFlag() {
    return flag;
  }

  public int getValue() {
    return value;
  }

  public int getDepth() {
    return depth;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CachedValue)) {
      return false;
    }
    CachedValue that = (CachedValue) o;
    return getValue() == that.getValue() &&
        getDepth() == that.getDepth() &&
        getFlag() == that.getFlag();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFlag(), getValue(), getDepth());
  }

  @Override
  public String toString() {
    return "CachedValue{" +
        "flag=" + flag +
        ", value=" + value +
        ", depth=" + depth +
        '}';
  }

  public static enum Flag {
    EXACT,
    LOWERBOUND,
    UPPERBOUND
  }
}
