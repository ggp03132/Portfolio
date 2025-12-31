// 전역적으로 CSRF 토큰 및 헤더 이름을 가져오는 함수 (만약 <meta> 태그가 있다면)
			    function getCsrfToken() {
			        const tokenMeta = document.querySelector('meta[name="_csrf"]');
			        const headerMeta = document.querySelector('meta[name="_csrf_header"]');
			        
			        // CSRF 메타 태그가 없으면 null 반환 (하지만 Spring Security 사용 시 권장되지 않음)
			        if (!tokenMeta || !headerMeta) {
			            console.error("CSRF meta tags not found.");
			            return null;
			        }

			        return {
			            token: tokenMeta.content,
			            headerName: headerMeta.content
			        };
			    }

			    // 취소 버튼 클릭 시 모달의 "취소하기" 버튼에 실제 폼 액션을 연결하는 함수
			    function setCancelAction(button) {
			        const applyNo = button.getAttribute('data-applyno');
			        const confirmBtn = document.getElementById('confirmCancelBtn');

			        // 버튼 클릭 시 동적으로 폼을 생성하여 전송하는 로직
			        confirmBtn.onclick = function() {
			            const form = document.createElement('form');
			            form.method = 'post';
			            form.action = `/apply/cancel/${applyNo}`; 

			            // 1. CSRF 토큰 정보 가져오기
			            const csrf = getCsrfToken();

			            if (csrf) {
			                // 2. _csrf 토큰을 숨겨진 입력 필드로 폼에 추가
			                const csrfInput = document.createElement('input');
			                csrfInput.type = 'hidden';
			                csrfInput.name = '_csrf'; // Spring Security가 기대하는 파라미터 이름
			                csrfInput.value = csrf.token;
			                form.appendChild(csrfInput);
			            }

			            document.body.appendChild(form);
			            form.submit();
			        };
			        
			        // 모달을 닫는 역할도 함께 수행
			        const cancelModal = bootstrap.Modal.getInstance(document.getElementById('cancelModal'));
			        if (cancelModal) {
			             cancelModal.hide();
			        }
			    }