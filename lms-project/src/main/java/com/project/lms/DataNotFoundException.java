package com.project.lms;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//이 예외는 보통 서비스 로직에서 특정 조건에 맞는 데이터가 없을 때 사용됨

/*특정 예외(Exception) 클래스에 HTTP 응답 상태를 자동으로 지정하기 위해 사용하는 어노테이션입니다
클라이언트(예: 웹 브라우저, 모바일 앱)에게 **"요청한 리소스를 찾을 수 없다"**는 의미의 404 Not Found HTTP 상태 코드를 보내도록 설정합니다.
이 어노테이션을 예외 클래스에 붙여두면, 컨트롤러에서 해당 예외를 던지기만 해도 Spring이 알아서 404 응답을 생성해주므로 코드가 간결해집니다.*/
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "엔티티가 없음")
//런타임예외 상속의 의미는 개발자가 반드시 try-catch 문으로 예외 처리를 하거나 throws로 예외를 명시하지 않아도 컴파일 오류가 발생하지 않습니다.
public class DataNotFoundException extends RuntimeException {
	/*
	 * 이 필드는 직렬화(Serialization)와 관련된 버전 관리용 고유 ID입니다. 클래스의 내용이 바뀌어도 이 ID가 같으면 이전
	 * 버전에서 직렬화된 객체를 새 버전의 클래스로 역직렬화할 수 있습니다. 예외 클래스에 포함하는 것이 좋은 습관입니다.
	 */	
	private static final long serialVersionUID =1L;

	/*
	 * 이 부분은 DataNotFoundException 객체를 생성할 때 사용되는 생성자(Constructor)입니다. 
	 * String message: 예외가 발생한 구체적인 원인을 설명하는 메시지를 전달받습니다. 예를 들어,
	 * "ID가 'user123'인 사용자를 찾을 수 없습니다."와 같은 메시지를 담을 수 있습니다.
	 */
	public DataNotFoundException(String message) {
		/*
		 * 이 코드는 부모 클래스인 RuntimeException의 생성자를 호출합니다. 전달받은 message를 부모 클래스로
		 * 넘겨주어 예외 객체 내에 오류 메시지를 저장하게 합니다. 
		 * 이렇게 저장된 메시지는 나중에 e.getMessage()와 같은 메소드로 조회할 수 있습니다
		 */
		super(message);
	}
}