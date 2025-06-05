# 🤖 Commit & PR Agent (한글 기반 자동화)

ALOC 프로젝트에서는 브랜치 네이밍, 커밋 메시지, PR 설명을 일관된 스타일로 관리하기 위해  
**한글 기반 Commit & PR 자동화 에이전트**를 정의하여 사용합니다.

---

## 🧠 에이전트 역할

이 Agent는 아래 작업을 자동으로 수행합니다:

1. 브랜치 네이밍 생성 (`feat/~`, `fix/~` 등)
2. gitmoji 기반 한글 커밋 메시지 생성
3. PR 요약 서머리를 **한글**로 자동 작성

---

## 🗂️ 브랜치 네이밍 규칙

형식: `<타입>/<기능명-또는-작업내용>`

| 타입       | 설명                  |
|------------|-----------------------|
| `feat`     | 기능 추가              |
| `fix`      | 버그 수정              |
| `refactor` | 리팩토링              |
| `docs`     | 문서 작업              |
| `chore`    | 설정, 배포 등 기타 작업 |

예시:
```
feat/user-course-문제순서저장  
fix/응시버튼-중복클릭-버그
```

---

## 💬 커밋 메시지 규칙

형식: `<gitmoji> <타입>: <한글 요약>`

예시:
```
🩹 Fix: userCourseProblem 의 of에 problemOrder 추가  
✨ Feat: 문제 생성 시 순서 정보 포함  
📝 Docs: agent.md 작성
```

> gitmoji는 [gitmoji.dev](https://gitmoji.dev) 기준 사용

---

## 🧾 PR 서머리 자동 생성 규칙

입력 커밋 메시지를 기반으로 아래와 같은 형식으로 자동 생성합니다.

### 커밋 메시지:
```
🩹 Fix: userCourseProblem 의 of에 problemOrder 추가
```

### 생성된 PR 서머리:
```md
## 🩹 Fix: userCourseProblem 의 of에 problemOrder 추가

### 📌 개요
- userCourseProblem 객체 생성 시 사용하는 of 메서드에 problemOrder 파라미터가 누락된 문제를 수정했습니다.

### 🔧 변경 사항
- of 메서드 파라미터에 problemOrder 추가
- 생성 시점에 순서 정보가 정확히 반영되도록 수정

### ✅ 체크리스트
- [x] 관련 코드 수정
- [x] 기능 정상 작동 확인
- [x] 기존 로직에 영향 없음 확인
```

---

## ⚙️ Agent 입력 형식 예시

에이전트는 다음과 같은 입력만 받으면 동작합니다:

```json
{
  "commit": "🩹 Fix: userCourseProblem 의 of에 problemOrder 추가"
}
```

이 커밋 메시지를 기반으로 브랜치 추천, 커밋 생성, PR 서머리를 자동 구성합니다.

---

## 📌 기타

- 한글을 기본으로 사용하여 팀 내 커뮤니케이션을 강화합니다.
- 모든 PR은 에이전트가 생성한 `.md` 템플릿을 기반으로 작성합니다.
- 추후 GitHub Action 또는 CLI 툴로 통합 가능 예정

---

> 본 Agent는 ALOC 프로젝트 개발 생산성과 커뮤니케이션 효율을 높이기 위해 설계되었습니다.
