package config;

import lombok.Getter;
import lombok.Setter;

public class Props {
    @Getter
    @Setter
    private static String botToken;
    @Getter
    @Setter
    private static String mongoHost;
    @Getter
    @Setter
    private static int mongoPort;
}
