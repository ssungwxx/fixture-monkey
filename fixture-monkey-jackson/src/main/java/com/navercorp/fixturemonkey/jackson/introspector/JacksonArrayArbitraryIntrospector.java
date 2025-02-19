/*
 * Fixture Monkey
 *
 * Copyright (c) 2021-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.fixturemonkey.jackson.introspector;

import java.lang.reflect.Type;

import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.navercorp.fixturemonkey.api.generator.ArbitraryGeneratorContext;
import com.navercorp.fixturemonkey.api.introspector.ArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ArbitraryIntrospectorResult;
import com.navercorp.fixturemonkey.api.introspector.ListIntrospector;
import com.navercorp.fixturemonkey.api.matcher.Matcher;
import com.navercorp.fixturemonkey.api.property.Property;
import com.navercorp.fixturemonkey.api.type.Types;
import com.navercorp.fixturemonkey.jackson.FixtureMonkeyJackson;
import com.navercorp.fixturemonkey.jackson.type.JacksonTypeReference;

@API(since = "0.5.5", status = Status.MAINTAINED)
public final class JacksonArrayArbitraryIntrospector implements ArbitraryIntrospector, Matcher {
	public static final JacksonArrayArbitraryIntrospector INSTANCE = new JacksonArrayArbitraryIntrospector(
		FixtureMonkeyJackson.defaultObjectMapper()
	);
	private static final ArbitraryIntrospector DELEGATOR = new ListIntrospector();

	private final ObjectMapper objectMapper;

	public JacksonArrayArbitraryIntrospector(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public boolean match(Property property) {
		return Types.getActualType(property.getType()).isArray();
	}

	@Override
	public ArbitraryIntrospectorResult introspect(ArbitraryGeneratorContext context) {
		Property property = context.getResolvedProperty();
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType elementType = typeFactory.constructType(new JacksonTypeReference<Object>() {
			@Override
			public Type getType() {
				return Types.getArrayComponentAnnotatedType(property.getAnnotatedType()).getType();
			}
		});

		ArrayType arrayType = TypeFactory.defaultInstance()
			.constructArrayType(elementType);

		return new ArbitraryIntrospectorResult(
			new JacksonCombinableArbitrary<>(
				DELEGATOR.introspect(context).getValue(),
				list -> objectMapper.convertValue(list, arrayType)
			)
		);
	}
}
