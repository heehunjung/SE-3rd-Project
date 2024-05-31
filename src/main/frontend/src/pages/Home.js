import React, { useEffect, useState } from 'react';
import {Nav, Container, Badge, Tab, Tabs} from 'react-bootstrap';
import Navbar from 'react-bootstrap/Navbar';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import {Link, useParams} from "react-router-dom";
import '../App.css';

const Home = () => {
    const { id } = useParams();
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);
    const [stockData, setStockData] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/stockData`)
            .then(response => {
                if(!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data=>{
                setStockData(data);
            })
            .catch(error=>setError(error.message));
    },[]);

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
            <br/>
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
                            <Card.Header>
                                <Tabs defaultActiveKey="all" transition={false} id="noanim-tab-example" className="mb-3">
                                    <Tab eventKey="all" title="전체 주식">
                                        <Card.Body className="scrollable-card">
                                            {stockData ? (
                                                stockData.map(stock => (
                                                    <p key={stock.id}>
                                                        <Badge bg="success">주식 이름: {stock.stockName} </Badge>
                                                        <Badge bg="secondary">종목 코드: {stock.stockSymbol}</Badge>
                                                        {stock.content ? <strong>{stock.content}</strong> : <strong>정보 미제공</strong>}
                                                    </p>
                                                ))
                                            ) : error ? (
                                                <p>오류: {error}</p>
                                            ) : (
                                                <p>데이터를 불러오는 중...</p>
                                            )}
                                        </Card.Body>
                                    </Tab>
                                    <Tab eventKey="today" title="오늘의 주식">
                                    </Tab>
                                </Tabs>
                            </Card.Header>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </>
    );
}

export default Home;
