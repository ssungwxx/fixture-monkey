package com.navercorp.fixturemonkey.arbitrary;
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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.BDDAssertions.then;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

class ArbitraryUtilsTest {
	@Property(tries = 10)
	void uuid(@ForAll @IntRange(min = 2, max = 100000) int size) {
		Set<UUID> actual = ArbitraryUtils.uuid()
			.sampleStream()
			.limit(size)
			.collect(toSet());
		then(actual).hasSize(size);
	}

	@Property(tries = 10)
	void currentTime(@ForAll @IntRange(min = 2, max = 100000) int size) {
		// when
		List<Instant> actual = ArbitraryUtils.currentTime()
			.sampleStream()
			.limit(size)
			.collect(toList());

		// then
		Instant now = Instant.now();
		actual.forEach(it ->
			then(it).isBetween(now.minus(10, ChronoUnit.SECONDS), now)
		);
	}

	@Property(tries = 10)
	void list(@ForAll @IntRange(min = 2, max = 100000) int size) {
		// given
		Arbitrary<String> arbitrary = Arbitraries.strings().all();

		// when
		List<String> actual = ArbitraryUtils.list(arbitrary, size);

		then(actual).hasSize(size);
	}
}
