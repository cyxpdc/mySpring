package com.pdc.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类名可以随意, 字段需要保持一致
 * 因为为json传输，不会带上类名
 * @author 晓风轻
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

	private String id;

	private String name;

	private int age;

}
