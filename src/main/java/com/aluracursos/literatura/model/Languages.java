package com.aluracursos.literatura.model;

public enum Languages {

    EN("English", "Inglés", "Ingles"),
    ES("Spanish", "Español", "Espanol"),
    FR("French", "Francés", "Frances"),
    DE("German", "Alemán", "Aleman"),
    IT("Italian", "Italiano", "Italiano"),
    PT("Portuguese", "Portugués", "Portugues");

    private final String languagesOmdb; // Nombre en inglés
    private final String languagesEspanol; // Nombre en español (con tildes)
    private final String languagesSinTilde; // Nombre en español (sin tildes)

    Languages(final String languagesOmdb, final String languagesEspanol, final String languagesSinTilde) {
        this.languagesOmdb = languagesOmdb;
        this.languagesEspanol = languagesEspanol;
        this.languagesSinTilde = languagesSinTilde;
    }

    public static Languages fromInput(final String text) {
        for (Languages language : Languages.values()) {
            if (language.name().equalsIgnoreCase(text) ||
                    language.languagesOmdb.equalsIgnoreCase(text) ||
                    language.languagesEspanol.equalsIgnoreCase(text) ||
                    language.languagesSinTilde.equalsIgnoreCase(text)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Idioma no reconocido: " + text);
    }

    public String getLanguagesOmdb() {
        return languagesOmdb;
    }

    public String getLanguagesEspanol() {
        return languagesEspanol;
    }

    public String getLanguagesSinTilde() {
        return languagesSinTilde;
    }
}