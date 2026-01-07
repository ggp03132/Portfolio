function renderMiniCalendar() {
    const calendarEl = document.getElementById('miniCalendar');
    if (!calendarEl) return;

    // [1] 현재 날짜 설정 (오늘 날짜를 표시하고 강조)
    const today = new Date();
    
    // 현재 캘린더를 표시할 월을 오늘 날짜 기준으로 설정합니다.
    const currentYear = today.getFullYear();
    const currentMonth = today.getMonth(); // 0부터 시작 (0=1월)
    const todayDay = today.getDate(); // 오늘 날짜 (강조할 날짜)

    // 현재 달의 첫 날과 마지막 날 계산
    // currentMonth + 1 은 다음 달을 의미하며, day=0은 '다음 달의 0번째 날', 즉 '이번 달의 마지막 날'을 의미합니다.
    const firstDayOfMonth = new Date(currentYear, currentMonth, 1).getDay(); // 0(일요일) ~ 6(토요일)
    const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();
    
    // 요일 헤더
    const daysOfWeek = ['일', '월', '화', '수', '목', '금', '토'];
    let calendarHtml = `
        <h6 class="text-center fw-bold text-secondary">${currentYear}년 ${currentMonth + 1}월</h6>
        <table class="table table-sm table-borderless text-center small mb-0">
            <thead>
                <tr>
                    ${daysOfWeek.map(day => `<th class="${day === '일' ? 'text-danger' : (day === '토' ? 'text-primary' : '')}">${day}</th>`).join('')}
                </tr>							
            </thead>
            <tbody>
                <tr>
    `;

    // [2] 빈 칸 채우기 (해당 월의 시작 요일까지)
    for (let i = 0; i < firstDayOfMonth; i++) {
        calendarHtml += '<td></td>';
    }

    // [3] 날짜 채우기
    let dayCounter = 1;
    let cellCount = firstDayOfMonth;

    while (dayCounter <= daysInMonth) {
        if (cellCount % 7 === 0 && cellCount !== firstDayOfMonth) {
            calendarHtml += '</tr><tr>';
        }

		// 현재 요일 계산: 0=일요일, 6=토요일
		// new Date()를 매번 생성하는 대신, 반복문 내부에서 요일을 계산합니다.
		const dayIndex = new Date(currentYear, currentMonth, dayCounter).getDay();
		    
		// 날짜 강조 스타일
		let dayClass = '';
		    
		// 1. 오늘 날짜 강조 (가장 높은 우선순위)
		if (dayCounter === todayDay) { 
		    dayClass = 'bg-primary text-white fw-bold rounded-circle p-1'; 
		} 
		// 2. 일요일
		else if (dayIndex === 0) { // getDay() === 0 이면 일요일
		     dayClass = 'text-danger'; 
		} 
		// 3. 토요일
		else if (dayIndex === 6) { // getDay() === 6 이면 토요일
		    dayClass = 'text-primary'; 
		}
        
        calendarHtml += `<td><span class="${dayClass}">${dayCounter}</span></td>`;
        
        dayCounter++;
        cellCount++;
    }

    // [4] 남은 빈 칸 채우기
    while (cellCount % 7 !== 0) {
        calendarHtml += '<td></td>';
        cellCount++;
    }

    calendarHtml += `
                </tr>
            </tbody>
        </table>
    `;
    calendarEl.innerHTML = calendarHtml;
	
}
document.addEventListener('DOMContentLoaded', renderMiniCalendar);
