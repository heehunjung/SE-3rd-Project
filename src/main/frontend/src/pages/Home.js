import React, { useEffect, useState } from 'react';
import { Nav, Container } from 'react-bootstrap';
import Navbar from 'react-bootstrap/Navbar';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import {useParams} from "react-router-dom";
import '../App.css';

const Home = () => {
    const { id } = useParams();
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/memberInfo/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => setUserData(data))
            .catch(error => setError(error.message));
    }, [id]);


    return (
        <>
            <Navbar bg="dark" data-bs-theme="dark">
                <Container>
                    <Navbar.Brand href="/Home">kw거래소</Navbar.Brand>
                    <Nav className="ml-auto">
                        <Nav.Link href="/Home">홈 화면</Nav.Link>
                        <Nav.Link href="/Trading">주식 구매</Nav.Link>
                        <Nav.Link href="/Community">커뮤니티</Nav.Link>
                        <Nav.Link href="/MyInfo">내 정보</Nav.Link>
                    </Nav>
                </Container>
            </Navbar>
            <br />
            <Container>
                <Row>
                    <Col>
                        <Card>
                            <Card.Header>상승률 Top 10🔥</Card.Header>
                            <Card.Body>
                                <blockquote className="blockquote mb-0">
                                    <p>
                                        {userData ? (
                                            <div>
                                                <p>이름: {userData.name}</p>
                                                <p>아이디: {userData.username}</p>
                                                <p>잔액: {userData.balance}</p>
                                                <p>포트폴리오: {userData.portfolio}</p>
                                                <p>역할: {userData.role}</p>
                                            </div>
                                        ) : error ? (
                                            <p>오류: {error}</p>
                                        ) : (
                                            <p>데이터를 불러오는 중...</p>
                                        )}
                                    </p>
                                    <footer className="blockquote-footer">
                                    </footer>
                                </blockquote>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col>
                        <Card>
                            <Card.Header>하락률 Top 10🌧️</Card.Header>
                            <Card.Body>
                                <blockquote className="blockquote mb-0">
                                    <p>
                                        {' '}
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer
                                        posuere erat a ante.{' '}
                                    </p>
                                    <footer className="blockquote-footer">
                                        Someone famous in <cite title="Source Title">Source Title</cite>
                                    </footer>
                                </blockquote>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col>
                        <Card>
                            <Card.Header>내 관심종목</Card.Header>
                            <Card.Body>
                                <blockquote className="blockquote mb-0">
                                    <p>
                                        {' '}
                                        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer
                                        posuere erat a ante.{' '}
                                    </p>
                                    <footer className="blockquote-footer">
                                        Someone famous in <cite title="Source Title">Source Title</cite>
                                    </footer>
                                </blockquote>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
                <br/>
                <br/>
                <br/>
                <Row>
                    <Col>
                        <Card>
                            <Card.Header>게시물</Card.Header>
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

export default Home;
