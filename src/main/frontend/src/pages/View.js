import React, { useState, useEffect } from 'react';
import Navbar from "react-bootstrap/Navbar";
import { Container, Nav, Card, Spinner, Form, Button } from "react-bootstrap";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import './Board.css';

const View = () => {
    const { id } = useParams();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const memberId = queryParams.get('memberId');
    const [role, setRole] = useState("");
    const [boardData, setBoardData] = useState(null);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(true);
    const [comments, setComments] = useState(null);
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);
    const [newComment, setNewComment] = useState("");

    useEffect(() => {
        fetch(`http://localhost:8080/board/${id}`)
            .then(res => {
                if (!res.ok) {
                    throw new Error('Network response was not ok');
                }
                return res.json();
            })
            .then(data => {
                const updateData = { ...data, view: data.view + 1 };
                setBoardData(updateData);
                return fetch(`http://localhost:8080/viewCount/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                });
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error('Network response was not ok');
                }
                setLoading(false);
            })
            .catch(error => {
                setError(error.message);
                setLoading(false);
            });
    }, [id]);

    useEffect(() => {
        fetch(`http://localhost:8080/memberInfo/${memberId}`)
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text); });
                }
                return response.json();
            })
            .then(data => {
                setUserData(data);
                setRole(data.role);
            })
            .catch(error => {
                setError(error.message);
                alert(error.message);
            });
    }, [id]);

    const handleCommentSubmit = (e) => {
        e.preventDefault();
        if (!userData) {
            alert('유저 정보를 가져오는 중입니다. 잠시만 기다려주세요.');
            return;
        }
        const postData = {
            postId: id,
            nickname: userData.nickname,
            createdAt: new Date().toISOString(),
            content: newComment,
        };
        fetch(`http://localhost:8080/postComment`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(postData),
        })
            .then((response) => {
                if (response.status === 201 || response.status === 200) {
                    return response.text();
                } else {
                    return response.text().then(text => Promise.reject(text || '댓글 업로드에 실패하였습니다.'));
                }
            })
            .then((data) => {
                console.log(data);
                setNewComment("");
                navigate(`/ViewPost/${id}?memberId=${memberId}`);
            })
            .catch((error) => {
                console.error('Error:', error);
                alert(error);
            });
    };

    useEffect(() => {
        fetch(`http://localhost:8080/getComment/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                setComments(data);
            })
            .catch(error => {
                console.error('Error fetching comments:', error);
                setComments([]);
            });
    }, [id]);

    const handleEdit = () => {
        navigate(`/Post/${memberId}?postId=${id}`);
    };

    const handleDelete = () => {
        if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
            fetch(`http://localhost:8080/delete/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(() => {
                    alert("게시글이 삭제되었습니다.");
                    navigate(`/Board/${memberId}`);
                })
                .catch(error => setError(error.message));
        }
    };

    return (
        <>
            <Navbar bg="dark" variant="dark">
                <Container>
                    {userData && userData.role === 'ADMIN' ? (
                        <Navbar.Brand href={`/Home/${id}`}>KW 거래소📉 관리자 모드</Navbar.Brand>
                    ) : (
                        <Navbar.Brand href={`/Home/${id}`}>KW 거래소📉</Navbar.Brand>
                    )}
                    <Nav className="ml-auto">
                        <Nav.Link href={`/Home/${memberId}`}>홈 화면</Nav.Link>
                        <Nav.Link href={`/Trading/${memberId}`}>주식 구매</Nav.Link>
                        <Nav.Link href={`/Board/${memberId}`}>커뮤니티</Nav.Link>
                        <Nav.Link href={`/MyInfo/${memberId}`}>내 정보</Nav.Link>
                        <Nav.Link href={`/Post/${memberId}`}>게시글 작성</Nav.Link>
                        <Nav.Link href={'/Login'}>로그아웃</Nav.Link>
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
                                <div>
                                    ✍️{boardData.nickname} <span
                                    style={{fontSize: '0.8em', color: 'gray'}}>
                                                        👁️{boardData.view}
                                    </span>
                                </div>
                                <div>
                                    {(userData && userData.role === 'ADMIN') || (boardData.member.id === parseInt(memberId)) ? (
                                        <Button className="btn-icon" onClick={handleDelete}>
                                            ❌
                                        </Button>
                                    ) : null}
                                    {boardData.member.id === parseInt(memberId) && (
                                        <Button className="btn-icon" onClick={handleEdit}>
                                            ✏️
                                        </Button>
                                    )}
                                </div>
                            </div>
                        </Card.Header>
                        <Card.Body>
                            <blockquote className="blockquote mb-0">
                                <strong>{boardData.title}</strong>
                                <br/>
                                <br/>
                                <span style={{fontSize: '0.8em'}}>{boardData.content}</span>
                            </blockquote>
                        </Card.Body>
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
                            <Button type="submit" variant="primary">선플 달기</Button>
                        </Form>
                        <div className="scrollable-card">
                            {comments && comments.map((comment, index) => (
                                <div key={index}>
                                    <strong>🙋{comment.nickname}</strong> <span
                                    style={{fontSize: '0.8em', color: 'gray', marginLeft: '10px'}}>
                                        {new Date(comment.createdAt).toLocaleString()}
                                    </span>
                                    <p className="mb-1">{comment.content}</p>
                                    <br/>
                                </div>
                            ))}
                        </div>
                    </Card>
                )}
            </div>
        </>
    );
}

export default View;
