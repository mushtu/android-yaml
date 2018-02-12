package com.github.mushtu.android.yaml

import com.github.mushtu.android.yaml.ast.SettingsClass
import com.github.mushtu.android.yaml.ast.SettingsElement
import com.github.mushtu.android.yaml.ast.SettingsField
import com.github.mushtu.android.yaml.ast.SettingsList
import com.github.mushtu.android.yaml.ast.SettingsRootClass

class SettingsClassGenerator {
    public static SettingsElement buildAST(Map<String, Object> parsedYaml) {
        new SettingsRootClass(parsedYaml.collect { internalBuildAST([it.key], it.value).toTopLevel() })
    }

    private static SettingsElement internalBuildAST(List<String> keys, Object o) {
        if (o instanceof String ||
            o instanceof Integer ||
            o instanceof Double ||
            o instanceof Date ||
            o instanceof Boolean
        ) {
            return new SettingsField(keys.last(), o)
        } else if (o instanceof List) {
            def list = (o as List)
            def childClasses = list.collect { it.getClass() }.unique()
            if (childClasses.size() != 1) {
                throw new RuntimeException("Not supported list with mixed type: $childClasses")
            }

            def childKeys = keys
            if (list.first() instanceof Map) {
                childKeys = ["${keys.last()}_element"]
                if(keys.size() > 1) {
                    childKeys = keys[0..-2] + childKeys
                }
            }

            def children = list.collect { internalBuildAST(childKeys, it)}
            return new SettingsList(keys, children)
        } else if (o instanceof Map<String, Object>) {
            def children = (o as Map<String, Object>).collect { internalBuildAST(keys + [it.key] as List, it.value)}
            return new SettingsClass(keys, children)
        }

        throw new RuntimeException("Not supported type: ${o.getClass()}")
    }

}
