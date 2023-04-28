package be.hvwebsites.itembord.constants;

public enum FrequentieDateUnit {
    // NONe is voor items zndr frequentie
    DAYS("d"), WEEKS("w"), MONTHS("m"), YEARS("y"), NONE("n");

    private final String letter;
    FrequentieDateUnit(String letter){
        this.letter = letter;
    }

    public String getLetter(){
        return letter;
    }
}
