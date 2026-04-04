package br.com.buscamed.data.client.gemini.core.extensions

import com.google.genai.types.Schema
import com.google.genai.types.Type

fun Schema.Builder.string(nullable: Boolean? = null, enums: List<String>? = null): Schema {
    this.type(Type.Known.STRING)
    nullable?.let { this.nullable(it) }
    enums?.let { this.enum_(it) }
    return this.build()
}

fun Schema.Builder.number(nullable: Boolean? = null): Schema {
    this.type(Type.Known.NUMBER)
    nullable?.let { this.nullable(it) }
    return this.build()
}

fun Schema.Builder.boolean(nullable: Boolean? = null): Schema {
    this.type(Type.Known.BOOLEAN)
    nullable?.let { this.nullable(it) }
    return this.build()
}

fun Schema.Builder.obj(properties: Map<String, Schema>, nullable: Boolean? = null): Schema {
    this.type(Type.Known.OBJECT).properties(properties)
    nullable?.let { this.nullable(it) }
    return this.build()
}

fun Schema.Builder.array(items: Schema, nullable: Boolean? = null): Schema {
    this.type(Type.Known.ARRAY).items(items)
    nullable?.let { this.nullable(it) }
    return this.build()
}
