package com.moim.backend.global.common.exception;

import com.moim.backend.global.common.Result;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {

	private Result result;
	private String debug;

	public CustomException(Result result) {
		this.result = result;
		this.debug = result.getMessage();
	}
}
