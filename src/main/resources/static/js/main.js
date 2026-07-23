(() => {
    let yOffset = 0;        // 전체 문서의 스크롤값 (==pageYOffset)
    let sectionOffset = 0; // section 내의 스크롤 값

    let currentSection = 0; // 현재 섹션 번호

    let prevScrollHeight = 0; // 이전 섹션의 높이

    const sectionUI = [
        {   // 0번째 section의 UI정보
            heightMultiple: 4,
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
            heightMultiple: 4,
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

        },
        {   // 2번째 section의 UI정보
            heightMultiple: 4,
            scrollHeight: 0,
            elems: {
                section: document.querySelector('#scroll-section-2'),
                textList: document.querySelector('.change_textlist'),
                texts: document.querySelectorAll('.change_textlist > h1'),
            },
            values: {
                textOpacityIn: [],
                textOpacityOut: [],
            }
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
    function getCurrentSection() {
        let curSection = currentSection;

        if (yOffset > prevScrollHeight + sectionUI[curSection].scrollHeight) {
            curSection++;
        }
        else if (yOffset < prevScrollHeight) {
            curSection--;
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

        document.body.setAttribute('id', `show-section-${currentSection}`);
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


    let section2Played = false;
    let section2Timer = null;

    function startSection2TextBurst() {
        const s2 = sectionUI[2];
        const texts = s2.elems.texts;

        if (!texts || texts.length === 0) return;

        texts.forEach(t => t.style.opacity = 0);

        let idx = 0;

        if (section2Timer) clearInterval(section2Timer);

        const speed = 180;

        section2Timer = setInterval(() => {
            texts.forEach(t => t.style.opacity = 0);

            if (idx >= texts.length) {
                clearInterval(section2Timer); // 타이머 종료
                return;
            }

            texts[idx].style.opacity = 1;
            idx++;
        }, speed);
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

            case 2:
                if (!section2Played && scrollRatio > 0.15) {
                    startSection2TextBurst();
                    section2Played = true;
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
