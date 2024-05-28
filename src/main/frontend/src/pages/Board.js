import React, { useEffect, useState } from 'react';
import Navbar from "react-bootstrap/Navbar";
import { Container, Nav, Tab, Tabs, Row, Col, Card } from "react-bootstrap";
import { Link, useParams } from "react-router-dom";

const Board = () => {
    const { id } = useParams();
    const [error, setError] = useState(null);
    const [boardData, setBoardData] = useState([]);

    useEffect(() => {
        fetch(`http://localhost:8080/board/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => setBoardData(data))
            .catch(error => setError(error.message));
    }, [id]);

    const filterPosts = (boardType) => {
        return boardData.filter(post => post.board === boardType);
    };

    return (
        <>
            <Navbar bg="dark" variant="dark">
                <Container>
                    <Navbar.Brand href={`/Home/${id}`}>KW 거래소</Navbar.Brand>
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
            <Container>
                <Row>
                    <Col md={9}>
                        <Card>
                            <Card.Header>
                                <Tabs defaultActiveKey="all" transition={false} id="noanim-tab-example" className="mb-3">
                                    <Tab eventKey="all" title="전체 게시판">
                                        <Card.Body>
                                            {filterPosts(0).map(post => (
                                                <p key={post.id}>
                                                    <Link to={`/post/${post.id}`}>{post.title}</Link>
                                                </p>
                                            ))}
                                        </Card.Body>
                                    </Tab>
                                    <Tab eventKey="info" title="공지사항">
                                        <Card.Body>
                                            {filterPosts(1).map(post => (
                                                <p key={post.id}>
                                                    <Link to={`/post/${post.id}`}>{post.title}</Link>
                                                </p>
                                            ))}
                                        </Card.Body>
                                    </Tab>
                                    <Tab eventKey="discussion" title="종목 토론 방">
                                        <Card.Body>
                                            {filterPosts(2).map(post => (
                                                <p key={post.id}>
                                                    <Link to={`/post/${post.id}`}>{post.title}</Link>
                                                </p>
                                            ))}
                                        </Card.Body>
                                    </Tab>
                                    <Tab eventKey="free" title="자유 게시판">
                                        <Card.Body>
                                            {filterPosts(3).map(post => (
                                                <p key={post.id}>
                                                    <Link to={`/post/${post.id}`}>{post.title}</Link>
                                                </p>
                                            ))}
                                        </Card.Body>
                                    </Tab>
                                </Tabs>
                            </Card.Header>
                        </Card>
                    </Col>
                    <Col md={3}>
                        <Card>
                            <Card.Header>🔥인기게시물🔥</Card.Header>
                            <Card.Body>
                                <blockquote className="blockquote mb-0">
                                    <p>
                                        인기 게시물이 들어갈 자리
                                    </p>
                                </blockquote>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
                <br />
                <br />
                <br />
                <Row>
                    <Col>
                        <Card>
                            <Card.Header>🔀랜덤 주식 추천🔀</Card.Header>
                            <Card.Body>
                                <Card.Text>
                                    <p>게시글이 없습니다.</p>
                                </Card.Text>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </>
    );
}

export default Board;
