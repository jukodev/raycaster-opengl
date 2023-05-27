import lombok.Getter;
public record Color(@Getter float red, @Getter float green, @Getter float blue) { }
// These have to be floats to avoid multiple divisions every frame
