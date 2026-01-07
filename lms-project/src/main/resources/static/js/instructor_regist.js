function openRolePopup(memberno) {
    window.open(
        `/member/role/form/${memberno}`,
        '강사등록',
        'width=500,height=400,top=150,left=500,scrollbars=no,resizable=no'
    );

    if (!popup || popup.closed || typeof popup.closed === 'undefined') {
        alert("팝업이 차단되었습니다. 브라우저 설정을 확인해주세요.");
    }
}