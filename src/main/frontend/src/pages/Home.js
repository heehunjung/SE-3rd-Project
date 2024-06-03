import React, { useEffect, useState } from 'react';
import { Nav, Container, Badge, Tab, Tabs } from 'react-bootstrap';
import Navbar from 'react-bootstrap/Navbar';
import Card from 'react-bootstrap/Card';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import { Link, useParams } from "react-router-dom";
import '../App.css';

const Home = () => {
    const { id } = useParams(); // memberId를 path variable로 받음
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);
    const [stockData, setStockData] = useState([]);
    const [topGainers, setTopGainers] = useState([]);
    const [topLosers, setTopLosers] = useState([]);

    useEffect(() => {
        fetch(`http://localhost:8080/stockData`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                setStockData(data); // 전체 주식 데이터 설정
                calculateGainsAndLosses(data);
            })
            .catch(error => setError(error.message));
    }, []);

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

    const calculateGainsAndLosses = (stocks) => {
        Promise.all(stocks.map(stock =>
            fetch(`http://localhost:8080/changes/${stock.id}`)
                .then(response => response.json())
                .then(gain => ({
                    ...stock,
                    gain
                }))
                .catch(() => ({
                    ...stock,
                    gain: null
                }))
        )).then(results => {
            const sortedGainers = results
                .filter(stock => stock.gain !== null)
                .sort((a, b) => b.gain - a.gain)
                .slice(0, 10);
            setTopGainers(sortedGainers);

            const sortedLosers = results
                .filter(stock => stock.gain !== null)
                .sort((a, b) => a.gain - b.gain)
                .slice(0, 10);
            setTopLosers(sortedLosers);
        }).catch(err => setError(err.message));
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
            <br />
            <Container>
                <Row>
                    <Col>
                        <Card>
                            <Card.Header>상승률 Top 10🔥</Card.Header>
                            <Card.Body className="scrollable-card">
                                {topGainers.length > 0 ? (
                                    topGainers.map(stock => (
                                        <p key={stock.id}>
                                            <Link to={`/Trading/${id}?stockId=${stock.id}`} style={{ color: '#ffcc00', textDecoration: 'none', fontSize: '1.1em', fontWeight: 'bold' }}>
                                                {stock.stockName}
                                            </Link>
                                            <Badge bg="secondary" style={{ marginLeft: '10px' }}>종목 코드: {stock.stockSymbol}</Badge>
                                            <Badge bg="danger" style={{ marginLeft: '10px' }}>상승률: {(stock.gain * 100).toFixed(2)}%</Badge>
                                        </p>
                                    ))
                                ) : error ? (
                                    <p>오류: {error}</p>
                                ) : (
                                    <p>데이터를 불러오는 중...</p>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col>
                        <Card>
                            <Card.Header>하락률 Top 10🌧️</Card.Header>
                            <Card.Body className="scrollable-card">
                                {topLosers.length > 0 ? (
                                    topLosers.map(stock => (
                                        <p key={stock.id}>
                                            <Link to={`/Trading/${id}?stockId=${stock.id}`} style={{ color: '#ffcc00', textDecoration: 'none', fontSize: '1.1em', fontWeight: 'bold' }}>
                                                {stock.stockName}
                                            </Link>
                                            <Badge bg="secondary" style={{ marginLeft: '10px' }}>종목 코드: {stock.stockSymbol}</Badge>
                                            <Badge bg="info" style={{ marginLeft: '10px' }}>하락률: {(stock.gain * 100).toFixed(2)}%</Badge>
                                        </p>
                                    ))
                                ) : error ? (
                                    <p>오류: {error}</p>
                                ) : (
                                    <p>데이터를 불러오는 중...</p>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col>
                        <Card>
                            <Card.Header>내 관심종목</Card.Header>
                            <Card.Body className="scrollable-card">
                                <p>
                                    {' '}
                                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer
                                    posuere erat a ante.{' '}
                                </p>
                                <footer className="blockquote-footer">
                                    Someone famous in <cite title="Source Title">Source Title</cite>
                                </footer>
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
                            <Card.Header>
                                <Tabs defaultActiveKey="all" transition={false} id="noanim-tab-example" className="mb-3">
                                    <Tab eventKey="all" title="전체 주식">
                                        <Card.Body className="scrollable-card">
                                            {stockData.length > 0 ? (
                                                stockData.map(stock => (
                                                    <p key={stock.id}>
                                                        <Link to={`/Trading/${id}?stockId=${stock.id}`} style={{ color: '#ffcc00', textDecoration: 'none', fontSize: '1.1em', fontWeight: 'bold' }}>
                                                            {stock.stockName}
                                                        </Link>
                                                        <Badge bg="secondary" style={{ marginLeft: '10px' }}>종목 코드: {stock.stockSymbol}</Badge>
                                                        <br />
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
