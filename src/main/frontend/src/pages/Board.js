import React from 'react';
import Navbar from "react-bootstrap/Navbar";
import {Button, Container, Nav, Tab, Tabs} from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import Card from "react-bootstrap/Card";
import {Link} from "react-router-dom";

const Board = () => {
    return (
        <>
            <Navbar bg="dark" variant="dark">
                <Container>
                    <Navbar.Brand href="/Home">KW 거래소</Navbar.Brand>
                    <Nav className="ml-auto">
                        <Nav.Link href="/Home">홈 화면</Nav.Link>
                        <Nav.Link href="/Trading">주식 구매</Nav.Link>
                        <Nav.Link href="/Board">커뮤니티</Nav.Link>
                        <Nav.Link href="/MyInfo">내 정보</Nav.Link>
                        <Nav.Link href="/Post">게시글 작성</Nav.Link>
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
                                            <blockquote className="blockquote mb-0">
                                                <p>전체 게시판</p>
                                            </blockquote>
                                        </Card.Body>
                                    </Tab>
                                    <Tab eventKey="info" title="공지사항">
                                        <Card.Body>
                                            <blockquote className="blockquote mb-0">
                                                <p>공지 사항</p>
                                            </blockquote>
                                        </Card.Body>
                                    </Tab>
                                    <Tab eventKey="discussion" title="종목 토론 방">
                                        <Card.Body>
                                            <blockquote className="blockquote mb-0">
                                                <p>종목 토론 방</p>
                                            </blockquote>
                                        </Card.Body>
                                    </Tab>
                                    <Tab eventKey="free" title="자유 게시판">
                                        <Card.Body>
                                            <blockquote className="blockquote mb-0">
                                                <p>자유 게시판</p>
                                            </blockquote>
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
