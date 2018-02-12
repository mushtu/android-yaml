package com.github.mushtu.android.yaml.ast

interface SettingsElement {
    String generateSource()
    SettingsElement toTopLevel()
    String typeString()
    String name()
    void collectClassSources(Map<String, String> classSources)
}
