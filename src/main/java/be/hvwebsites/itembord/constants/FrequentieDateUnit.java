package be.hvwebsites.itembord.constants;

public enum FrequentieDateUnit {
    DAYS("d"), WEEKS("w"), MONTHS("m"), YEARS("y");

    private final String letter;
    FrequentieDateUnit(String letter){
        this.letter = letter;
    }

    public String getLetter(){
        return letter;
    }
}
