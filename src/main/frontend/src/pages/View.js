import React, { useState, useEffect } from 'react';
import Navbar from "react-bootstrap/Navbar";
import { Container, Nav, Card, Spinner, Form, Button } from "react-bootstrap";
import { useParams, useNavigate } from "react-router-dom";
import './Board.css'; // 추가된 스타일 시트

const View = () => {
    const { id } = useParams();
    const [role, setRole] = useState("");
    const [boardData, setBoardData] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true); // 로딩 상태
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState("");
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/board/${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error('Network response was not ok');
                }
                return res.json();
            })
            .then(data => {
                setBoardData(data);
                setLoading(false); // 데이터가 성공적으로 로드되면 로딩 상태를 false로 설정
            })
            .catch(error => {
                setError(error.message);
                setLoading(false); // 오류 발생 시 로딩 상태를 false로 설정
            });
    }, [id]);
/*
    useEffect(() => {
        fetch(`http://localhost:8080/board/${id}/comments`)
            .then(res => {
                if (!res.ok) {
                    throw new Error('Network response was not ok');
                }
                return res.json();
            })
            .then(data => {
                setComments(data);
            })
            .catch(error => setError(error.message));
    }, [id]);*/

    const handleCommentSubmit = (e) => {
        e.preventDefault();
        // 서버에 새 댓글을 게시합니다
        fetch(`http://localhost:8080/board/${id}/comments`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: newComment })
        })
            .then(res => res.json())
            .then(data => {
                setComments([...comments, data]);
                setNewComment("");
            })
            .catch(error => setError(error.message));
    };

    return (
        <>
            <Navbar bg="dark" variant="dark">
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
            <br />

            <div className="centered-container">
                {loading ? (
                    <Spinner animation="border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </Spinner>
                ) : error ? (
                    <div className="alert alert-danger">{error}</div>
                ) : (
                    <Card className="mb-4 shadow-sm card-custom">
                        <Card.Header>
                            <div className="d-flex justify-content-between align-items-center">
                                <h5 className="mb-0">{boardData.title}</h5>
                                <div>
                                    ✍️{boardData.nickname} <span
                                    style={{fontSize: '0.8em', color: 'gray', marginLeft: '10px'}}>
                                                        👁️{boardData.view}
                                    </span>
                                </div>
                            </div>
                        </Card.Header>
                        <Card.Body>
                            <blockquote className="blockquote mb-0">
                                <p>{boardData.content}</p>
                            </blockquote>
                        </Card.Body>
                        <Card.Footer>
                            <Form onSubmit={handleCommentSubmit}>
                                <Form.Group controlId="comment">
                                    <Form.Control
                                        type="text"
                                        placeholder="댓글을 입력해주세요"
                                        value={newComment}
                                        onChange={(e) => setNewComment(e.target.value)}
                                        className="mb-2"
                                    />
                                </Form.Group>
                                <Button type="submit" variant="primary">작성하기</Button>
                            </Form>
                            <ul className="list-unstyled mt-3">
                                {comments.map((comment, index) => (
                                    <li key={index} className="border-bottom py-2">
                                        <strong>{comment.author}</strong>
                                        <p className="mb-1">{comment.content}</p>
                                    </li>
                                ))}
                            </ul>
                        </Card.Footer>
                    </Card>
                )}
            </div>
        </>
    );
}

export default View;
