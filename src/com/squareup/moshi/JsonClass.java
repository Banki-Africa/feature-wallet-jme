/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.moshi;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.reflect.Type;

/** Customizes how a type is encoded as JSON. */
@Retention(RUNTIME)
@Documented
public @interface JsonClass {
  /**
   * True to trigger the annotation processor to generate an adapter for this type.
   *
   * <p>There are currently some restrictions on which types that can be used with generated
   * adapters:
   *
   * <ul>
   *   <li>The class must be implemented in Kotlin (unless using a custom generator, see {@link
   *       #generator()}).
   *   <li>The class may not be an abstract class, an inner class, or a local class.
   *   <li>All superclasses must be implemented in Kotlin.
   *   <li>All properties must be public, protected, or internal.
   *   <li>All properties must be either non-transient or have a default value.
   * </ul>
   */
  boolean generateAdapter();

  /**
   * An optional custom generator tag used to indicate which generator should be used. If empty,
   * Moshi's annotation processor will generate an adapter for the annotated type. If not empty,
   * Moshi's processor will skip it and defer to a custom generator. This can be used to allow other
   * custom code generation tools to run and still allow Moshi to read their generated JsonAdapter
   * outputs.
   *
   * <p>Requirements for generated adapter class signatures:
   *
   * <ul>
   *   <li>The generated adapter must subclass {@link JsonAdapter} and be parameterized by this
   *       type.
   *   <li>{@link Types#generatedJsonAdapterName} should be used for the fully qualified class name
   *       in order for Moshi to correctly resolve and load the generated JsonAdapter.
   *   <li>The first parameter must be a {@link Moshi} instance.
   *   <li>If generic, a second {@link Type Type[]} parameter should be declared to accept type
   *       arguments.
   * </ul>
   *
   * <p>Example for a class "CustomType":
   *
   * <pre>{@code
   * class CustomTypeJsonAdapter(moshi: Moshi, types: Array<Type>) : JsonAdapter<CustomType>() {
   *   // ...
   * }
   * }</pre>
   *
   * <p>To help ensure your own generator meets requirements above, you can use Moshi’s built-in
   * generator to create the API signature to get started, then make your own generator match that
   * expected signature.
   */
  String generator() default "";
}
