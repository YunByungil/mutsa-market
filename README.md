# 🎉 멋사마켓이란?
> 📝 여러분들이 많이 사용하고 있는 🥕당근마켓, 중고나라 등을 착안하여 여러분들만의 중고 제품 거래 플랫폼을 만들어보는 미니 프로젝트입니다.
사용자가 중고 물품을 자유롭게 올리고, 댓글을 통해 소통하며, 최종적으로 구매 제안에 대하여 수락할 수 있는 형태의 중고 거래 플랫폼의 백엔드를 만들어봅시다.

## 🎯멋사마켓 ERD
![img.png](img.png)
  
## 1️⃣ DAY 1 / 중고 물품 관리 요구사항  

<details>  
<summary>1일차 기능 요구 사항</summary>

### 기능 요구 사항 
> 📝 DAY 1️⃣ **중고 물품 관리** **6/29**  
> 1. 누구든지 중고 거래를 목적으로 물품에 대한 정보를 등록할 수 있다.  
    a. 이때 반드시 포함되어야 하는 내용은 **제목, 설명, 최소 가격, 작성자**이다.  
    b. 또한 사용자가 물품을 등록할 때, 비밀번호 항목을 추가해서 등록한다.  
    c. 최초로 물품이 등록될 때, 중고 물품의 상태는 **판매중** 상태가 된다.  
> 2. 등록된 물품 정보는 누구든지 열람할 수 있다.
    a. 페이지 단위 조회가 가능하다.  
    b. 전체 조회, 단일 조회 모두 가능하다.  
> 3. 등록된 물품 정보는 수정이 가능하다.  
    a. 이때, 물품이 등록될 때 추가한 비밀번호를 첨부해야 한다.  
> 4. 등록된 물품 정보에 이미지를 첨부할 수 있다.  
    a. 이때, 물품이 등록될 때 추가한 비밀번호를 첨부해야 한다.  
    b. 이미지를 관리하는 방법은 자율이다.  
> 5. 등록된 물품 정보는 삭제가 가능하다.  
    a. 이때, 물품이 등록될 때 추가한 비밀번호를 첨부해야 한다.  
</details>   

<details>
<summary>1일차 기능 목록</summary>  
  
### ✨ 기능 목록
- [ ] 아이템 등록, 수정, 삭제
  - [ ] 수정 -> 이미지 첨부 가능, 비밀번호 입력
  - [ ] 삭제 -> 비밀번호 입력
- [ ] 등록된 아이템 전체 조회, 단일 조회  

</details>

<details>
<summary>1일차 예외 목록</summary>  
  
### ✨ 예외 목록
- [ ] 제목, 설명, 최소 가격, 작성자, 비밀번호 미작성 시 예외 발생
    - [ ] 수정, 삭제 -> 비밀번호 틀렸을 때, 예외 발생
</details>

<hr>

