(() => {
    let yOffset = 0;        // 전체 문서의 스크롤값 (==pageYOffset)
    let sectionOffset = 0; // section 내의 스크롤 값

    let currentSection = 0; // 현재 섹션 번호

    let prevScrollHeight = 0; // 이전 섹션의 높이

    const sectionUI = [
        {   // 0번째 section의 UI정보
            // 쇼핑 영역이 아래에 이어지므로 연출 구간을 너무 길게 잡지 않는다
            heightMultiple: 3,
            scrollHeight: 0,
            elems: {
                section: document.querySelector('#scroll-section-0'),
                message: document.querySelector('.section0-message'),
                video: document.querySelector('#section0-video'),

            },
            values: {
                message_opacity_out: [1, 0, { start: 0.1, end: 0.7 }],
                video_opacity_out: [1, 0, { start: 0.71, end: 0.95 }],
            }

        },
        {   // 1번째 section의 UI정보
            // 쇼핑 영역이 아래에 이어지므로 연출 구간을 너무 길게 잡지 않는다
            heightMultiple: 3,
            scrollHeight: 0,
            elems: {
                canvasContainer: document.querySelector('.section1-canvas'),
                canvas: document.querySelector('#section1-canvas'),
                gc: document.querySelector('.section1-canvas > canvas').getContext('2d'),
                section: document.querySelector('#scroll-section-1'),
                messageA: document.querySelector('.section1-message.a'),
                messageB: document.querySelector('.section1-message.b'),
                messageC: document.querySelector('.section1-message.c'),
            },
            values: {
                imageCount: 451,
                canvasImages: [],
                canvas_opacity_in: [0, 1, { start: 0, end: 0.1 }],
                canvas_opacity_out: [1, 0, { start: 0.8, end: 0.95 }],
                canvas_image_index: [0, 450],

                messageA_opacity_in: [0, 1, { start: 0.11, end: 0.2 }],
                messageA_opacity_out: [1, 0, { start: 0.21, end: 0.3 }],
                messageA_transform: [0, -60, { start: 0.21, end: 0.3 }],

                messageB_opacity_in: [0, 1, { start: 0.31, end: 0.4 }],
                messageB_opacity_out: [1, 0, { start: 0.41, end: 0.5 }],
                messageB_transform: [0, -60, { start: 0.41, end: 0.5 }],

                messageC_opacity_in: [0, 1, { start: 0.51, end: 0.6 }],
                messageC_opacity_out: [1, 0, { start: 0.61, end: 0.7 }],
                messageC_transform: [0, -60, { start: 0.61, end: 0.7 }],
            },

        }];


    function setLayout() {

        // 현재 브라우저 화면의 높이.
        const viewHeight = window.innerHeight;

        for (let i = 0; i < sectionUI.length; i++) {
            sectionUI[i].scrollHeight = viewHeight * sectionUI[i].heightMultiple;
            sectionUI[i].elems.section.style.height = `${sectionUI[i].scrollHeight}px`;

        }

    }
    function getPrevScrollHeight() { // 이전 섹션의 길이의 총합
        let prevHeight = 0;

        for (let i = 0; i < currentSection; i++) {
            prevHeight += sectionUI[i].scrollHeight;
        }

        return prevHeight;
    }
    function getTotalScrollHeight() { // 스크롤 연출 구간 전체 길이
        let total = 0;

        for (let i = 0; i < sectionUI.length; i++) {
            total += sectionUI[i].scrollHeight;
        }

        return total;
    }
    function getCurrentSection() {
        let curSection = currentSection;

        if (yOffset > prevScrollHeight + sectionUI[curSection].scrollHeight) {
            curSection++;
        }
        else if (yOffset < prevScrollHeight) {
            curSection--;
        }

        // 연출 구간 아래로는 쇼핑 영역이 이어진다.
        // 범위를 넘기면 sectionUI[curSection] 이 없어 스크립트가 죽는다.
        if (curSection < 0) {
            curSection = 0;
        }
        else if (curSection > sectionUI.length - 1) {
            curSection = sectionUI.length - 1;
        }

        return curSection;

    }
    function getSectionOffset() {
        return yOffset - prevScrollHeight;
    }

    function scrolling() {
        yOffset = window.pageYOffset;
        // 이전 섹션의 높이를 저장한다
        prevScrollHeight = getPrevScrollHeight();

        // 현재 섹션의 번호를 가져온다
        currentSection = getCurrentSection();

        sectionOffset = getSectionOffset();

        // 연출 구간을 다 지나면 고정 요소를 모두 감춘다.
        // (일치하는 CSS 가 없는 id 를 주면 .sticky-element 는 display:none 상태로 남는다)
        if (yOffset >= getTotalScrollHeight()) {
            document.body.setAttribute('id', 'show-shop');
        }
        else {
            document.body.setAttribute('id', `show-section-${currentSection}`);
        }
    }

    function getAssignedValue(value) {
        let assigned = 0;
        const scrollHeight = sectionUI[currentSection].scrollHeight;

        // [1, 0, {start : 0.1, end : 0.9}]
        if (value.length === 3) {

            const partStart = value[2].start * scrollHeight;
            const partEnd = value[2].end * scrollHeight;
            const partHeight = partEnd - partStart;

            if (sectionOffset < partStart) {
                assigned = value[0];
            }
            else if (sectionOffset > partEnd) {
                assigned = value[1];
            }
            else if (sectionOffset >= partStart && sectionOffset <= partEnd) {
                const partLength = sectionOffset - partStart;
                const partRatio = partLength / partHeight;

                assigned = ((value[1] - value[0]) * partRatio) + value[0];
            }
            //else는 올 이유가 없음
        }
        else {
            // [1, 0]    

            // 1. 비율을 구해
            const ratio = sectionOffset / scrollHeight;

            // 2. 해당하는 css 값을 구한다
            assigned = ((value[1] - value[0]) * ratio) + value[0];

        }

        return assigned;
    }

    function drawCanvasImage(index) {
        const s1 = sectionUI[1];
        const gc = s1.elems.gc;
        const canvasWidth = gc.canvas.width;
        const canvasHeight = gc.canvas.height;

        // index 범위 체크
        if (index < 0) return;
        if (index >= s1.values.imageCount) index = s1.values.imageCount - 1;

        // 이미 로드된 이미지면 바로 그리기
        if (s1.values.canvasImages[index] && s1.values.canvasImages[index].complete) {
            gc.clearRect(0, 0, canvasWidth, canvasHeight);
            gc.drawImage(s1.values.canvasImages[index], 0, 0, canvasWidth, canvasHeight);
            return;
        }

        // 아직 없는 이미지면 새로 로드
        const img = new Image();
        img.src = `/media/frames/drum_${index}.jpg`;

        img.onload = function () {
            s1.values.canvasImages[index] = img; // 캐시에 저장
            gc.clearRect(0, 0, canvasWidth, canvasHeight);
            gc.drawImage(img, 0, 0, canvasWidth, canvasHeight);
        };

        img.onerror = function () {
            console.warn(`Failed to load image: drum_${index}.jpg`);
        };
    }


    function playAnimation() {
        let opacity = 0;
        let imageIndex = 0;
        let ty = 0;

        const scrollRatio = sectionOffset / sectionUI[currentSection].scrollHeight;
        const values = sectionUI[currentSection].values;
        const elems = sectionUI[currentSection].elems;

        switch (currentSection) {
            case 0:
                opacity = getAssignedValue(values.message_opacity_out);
                elems.message.style.opacity = opacity;

                opacity = getAssignedValue(values.video_opacity_out);
                elems.video.style.opacity = opacity;
                break;

            case 1:
                const imageIndex = Math.floor(scrollRatio * (values.canvas_image_index[1]));
                drawCanvasImage(imageIndex);


                elems.messageA.style.opacity = 0;
                elems.messageB.style.opacity = 0;
                elems.messageC.style.opacity = 0;

                if (scrollRatio <= 0.1) {
                    opacity = getAssignedValue(values.canvas_opacity_in);
                    elems.canvasContainer.style.opacity = opacity;
                }
                else if (scrollRatio < 0.21) {
                    opacity = getAssignedValue(values.messageA_opacity_in);
                    elems.messageA.style.opacity = opacity;
                }
                else if (scrollRatio < 0.31) {
                    opacity = getAssignedValue(values.messageA_opacity_out);
                    elems.messageA.style.opacity = opacity;
                    ty = getAssignedValue(values.messageA_transform);
                    elems.messageA.style.transform = `translateY(${ty}%)`;
                }
                else if (scrollRatio < 0.41) {
                    opacity = getAssignedValue(values.messageB_opacity_in);
                    elems.messageB.style.opacity = opacity;
                }
                else if (scrollRatio < 0.51) {
                    opacity = getAssignedValue(values.messageB_opacity_out);
                    elems.messageB.style.opacity = opacity;
                    ty = getAssignedValue(values.messageB_transform);
                    elems.messageB.style.transform = `translateY(${ty}%)`;
                }
                else if (scrollRatio < 0.61) {
                    opacity = getAssignedValue(values.messageC_opacity_in);
                    elems.messageC.style.opacity = opacity;
                }
                else if (scrollRatio < 0.71) {
                    opacity = getAssignedValue(values.messageC_opacity_out);
                    elems.messageC.style.opacity = opacity;
                    ty = getAssignedValue(values.messageC_transform);
                    elems.messageC.style.transform = `translateY(${ty}%)`;
                }
                else if (scrollRatio <= 0.96) {
                    opacity = getAssignedValue(values.canvas_opacity_out);
                    elems.canvasContainer.style.opacity = opacity;
                }
                else {
                    elems.messageA.style.opacity = 0;
                    elems.messageB.style.opacity = 0;
                    elems.messageC.style.opacity = 0;
                }
                break;

        }

    }
    /////////////////////////////////////////////
    // 이벤트 핸들러
    window.addEventListener('resize', (event) => {
        setLayout();
    });

    window.addEventListener('scroll', (event) => {
        scrolling();

        playAnimation();
    });

    /////////////////////////////////////////////
    // 함수 호출부.
    setLayout(); // 레이아웃 설정
    scrolling(); // 스크롤 1회 발생
    playAnimation();
})();
