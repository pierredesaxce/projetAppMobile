package fr.uavignon.ceri.tp3.data;

import java.util.List;

public class WeatherResponse {

    public final Main main=null;
    public final Wind wind=null;
    public final List<Detail> weather=null;
    public final Cloud clouds=null;

    public final Integer dt=null;


    public static class Main {
        public final Float temp= null;
        public final Integer humidity= null;
    }

    public static class Wind {
        public final Float speed= null;
        public final Integer deg= null;
    }

    public static class Detail {
        public final String description= null;
        public final String icon= null;
    }

    public static class Cloud {
        public final Integer all= null;
    }

}

