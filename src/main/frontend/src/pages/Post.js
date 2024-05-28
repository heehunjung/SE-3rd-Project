import React, { useState, useEffect } from 'react';
import Navbar from "react-bootstrap/Navbar";
import { Container, Form, Nav, Button } from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";

const Post = () => {
    const { id } = useParams();
    const [formData, setFormData] = useState({
        title: '',
        content: '',
        board: '',
        view: 0
    });
    const [role, setRole] = useState("");
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    // 유저 권한 정보를 가져오는 메소드
    useEffect(() => {
        fetch(`http://localhost:8080/memberInfo/${id}`)
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text); });
                }
                return response.json();
            })
            .then(data => {
                setUserData(data);
                setRole(data.role); // 멤버 객체에서 역할 정보 설정
            })
            .catch(error => {
                setError(error.message);
                alert(error.message); // 오류 메시지를 알림으로 표시
            });
    }, [id]);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!userData) {
            alert('유저 정보를 가져오는 중입니다. 잠시만 기다려주세요.');
            return;
        }
        const postData = {
            member: userData,
            nickname: userData.nickname,
            createdAt: new Date().toISOString(), // 현재 시간을 created_at 필드에 추가
            ...formData,
        };
        fetch("http://localhost:8080/post", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
            },
            body: JSON.stringify(postData),
        })
            .then((response) => {
                if (response.status === 201) {
                    return response.json();
                } else {
                    return response.json().then(err => Promise.reject(err.message || '게시글 업로드에 실패하였습니다.'));
                }
            })
            .then((data) => {
                console.log(data);
                navigate('/board/' + id);
            })
            .catch((error) => {
                console.error('Error:', error);
                alert(error);
            });
    };

    return (
        <>
            <Navbar bg="dark" data-bs-theme="dark">
                <Container>
                    <Navbar.Brand href={`/Home/${id}`}>KW 거래소📉</Navbar.Brand>
                    <Nav className="ml-auto">
                        <Nav.Link href={`/Home/${id}`}>홈 화면</Nav.Link>
                        <Nav.Link href={`/Trading/${id}`}>주식 구매</Nav.Link>
                        <Nav.Link href={`/Board/${id}`}>커뮤니티</Nav.Link>
                        <Nav.Link href={`/MyInfo/${id}`}>내 정보</Nav.Link>
                        <Nav.Link href={`/Post/${id}`}>게시글 작성</Nav.Link>
                    </Nav>
                </Container>
            </Navbar>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                    <Form.Label>제목</Form.Label>
                    <Form.Control
                        type="text"
                        placeholder="제목을 입력해주세요"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                    <Form.Label>내용</Form.Label>
                    <Form.Control
                        as="textarea"
                        rows={5}
                        name="content"
                        value={formData.content}
                        onChange={handleChange}
                    />
                </Form.Group>
                <Form.Group className="mb-3" controlId="exampleForm.ControlSelect1">
                    <Form.Label>게시판 선택</Form.Label>
                    <Form.Control
                        as="select"
                        name="board"
                        value={formData.board}
                        onChange={handleChange}
                    >
                        <option value="">게시판을 선택해주세요</option>
                        <option value="2">종목토론방</option>
                        <option value="3">자유게시판</option>
                        {role === "ADMIN" && (
                            <option value="1">공지사항</option>
                        )}
                    </Form.Control>
                </Form.Group>
                <Button variant="primary" type="submit">
                    제출
                </Button>
            </Form>
        </>
    );
}

export default Post;
