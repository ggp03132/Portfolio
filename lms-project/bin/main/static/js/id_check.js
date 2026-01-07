// 아이디 중복확인 js (memberidInput ID를 th:field 속성에서 명시적으로 추가했으므로 작동)
document.addEventListener("DOMContentLoaded", function() {
    const checkBtn = document.getElementById("checkIdBtn");
    // memberidInput ID는 th:field가 있는 input 태그에 직접 지정되었습니다.
    const memberidInput = document.getElementById("memberid"); 
    const idMessage = document.getElementById("idMessage");

    checkBtn.addEventListener("click", function() {
        const memberid = memberidInput.value.trim();

        if(memberid === "") {
            idMessage.textContent = "아이디를 입력해주세요.";
            idMessage.style.color = "red";
            return;
        }

        fetch(`/member/check_id?memberid=${encodeURIComponent(memberid)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버 오류");
                }
                return response.json();
            })
            .then(isDuplicated => {
                if(isDuplicated) {
                    idMessage.textContent = "이미 사용 중인 아이디입니다.";
                    idMessage.style.color = "red";
                } else {
                    idMessage.textContent = "사용 가능한 아이디입니다.";
                    idMessage.style.color = "green";
                }
            })
            .catch(error => {
                console.error("에러 발생:", error);
                idMessage.textContent = "서버와 통신 중 오류가 발생했습니다.";
                idMessage.style.color = "red";
            });
    });
});