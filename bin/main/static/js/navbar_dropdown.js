document.addEventListener('DOMContentLoaded', () => {
    // 드롭다운 이동
    const dropdown = document.getElementById('courseDropdown');
    if (dropdown) {
      dropdown.addEventListener('change', () => {
        const url = dropdown.value;
        if (url) window.location.href = url;
      });
    }

    // 햄버거 collapse 토글
    const toggleButton = document.getElementById('navbarToggle');
    const navbarContent = document.getElementById('navbarContent');

    toggleButton.addEventListener('click', (e) => {
      e.preventDefault();
      e.stopPropagation();
      navbarContent.classList.toggle('show');
    });

    // collapse 외 클릭 시 닫기
    document.addEventListener('click', (e) => {
      const isClickInside = navbarContent.contains(e.target) || toggleButton.contains(e.target);
      if (!isClickInside && navbarContent.classList.contains('show')) {
        navbarContent.classList.remove('show');
      }
    });

    // 창 크기 변경 시 992px 이상이면 닫기
    window.addEventListener('resize', () => {
      if (window.innerWidth > 991 && navbarContent.classList.contains('show')) {
        navbarContent.classList.remove('show');
      }
    });
  });