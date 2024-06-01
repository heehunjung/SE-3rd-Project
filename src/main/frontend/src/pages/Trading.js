import React, { useEffect, useState } from 'react';
import { Nav, Container, Form, Button, Row, Col, Card, Badge } from 'react-bootstrap';
import Navbar from 'react-bootstrap/Navbar';
import { useLocation, useParams } from "react-router-dom";
import ReactApexChart from 'react-apexcharts';
import '../App.css';

const Trading = () => {
    const { id } = useParams();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const stockId = queryParams.get('stockId') ?? '1';  // 기본값을 1로 설정
    const [stock, setStock] = useState(null);
    const [stockPrice, setStockPrice] = useState([]);
    const [error, setError] = useState(null);
    const [change, setChange] = useState(null);

    // 해당 주식 데이터 가져옴
    useEffect(() => {
        fetch(`http://localhost:8080/stockData/${stockId}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => { throw new Error(text); });
                }
                return res.json();
            })
            .then(data => {
                setStock(data);
                getUpAndDown(data.id);
            })
            .catch(error => {
                setError(error.message);
                alert(error.message);
            });
    }, [stockId]);

    // 해당 주소의 1년치 가격을 가져옴
    useEffect(() => {
        fetch(`http://localhost:8080/stockPrice/${stockId}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => { throw new Error(text); });
                }
                return res.json();
            })
            .then(data => {
                setStockPrice(data.map(item => ({ x: new Date(item.date), y: item.closingPrice })));
            })
            .catch(error => {
                setError(error.message);
                alert(error.message);
            });
    }, [stockId]);

    const getUpAndDown = (stockId) => {
        fetch(`http://localhost:8080/changes/${stockId}`)
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => { throw new Error(text); });
                }
                return response.json();
            })
            .then(data => {
                setChange(data);
            })
            .catch(error => {
                setError(error.message);
                alert(error.message);
            });
    };

    // ApexCharts 옵션 설정
    const chartOptions = {
        series: [{
            name: '종가',
            data: stockPrice
        }],
        options: {
            chart: {
                type: 'area',
                stacked: false,
                height: 400,
                zoom: {
                    type: 'x',
                    enabled: true,
                    autoScaleYaxis: true
                },
                toolbar: {
                    autoSelected: 'zoom'
                }
            },
            dataLabels: {
                enabled: false
            },
            markers: {
                size: 0,
            },
            fill: {
                type: 'gradient',
                gradient: {
                    shadeIntensity: 1,
                    inverseColors: false,
                    opacityFrom: 0.5,
                    opacityTo: 0,
                    stops: [0, 90, 100]
                },
            },
            yaxis: {
                labels: {
                    formatter: function (val) {
                        return val.toFixed(2);
                    }
                },
                title: {
                    text: '가격'
                },

            },
            xaxis: {
                type: 'datetime',
                labels: {
                    datetimeFormatter: {
                        year: 'yyyy년',
                        month: 'MM월',
                        day: 'dd일',
                        hour: 'HH시',
                        minute: 'mm분'
                    }
                }
            },
            tooltip: {
                shared: false,
                y: {
                    formatter: function (val) {
                        return val.toFixed(2)
                    }
                }
            },
            theme: {
                mode: 'dark'  // 다크 모드 테마
            }
        },
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
                        <div className="form-container">
                            <Form.Control size="lg" type="text" placeholder="주식 이름" />
                            <Button className="btn-icon2" type="submit">🔍</Button>
                        </div>
                    </Col>
                </Row>
                <Row>
                    <Col md={7}>
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Title>
                                <Card.Header>
                                    {error && <p>오류: {error}</p>}
                                    {!stock && !error && <p>데이터를 불러오는 중...</p>}
                                    {stock && (
                                        <>
                                            <h3>{stock.stockName}</h3>
                                            <div style={{display: 'flex', alignItems: 'center'}}>
                                                {stock.currentPrice}원
                                                <Badge
                                                    bg={change > 0 ? 'danger' : 'primary'}
                                                    style={{marginLeft: '10px'}}
                                                >
                                                    {change !== null ? (change > 0 ? '📈' : '📉') + (change * 100).toFixed(2) : 'N/A'}%
                                                </Badge>
                                            </div>
                                        </>
                                    )}
                                </Card.Header>
                            </Card.Title>
                            <Card.Body>
                                <div style={{height: 370}}>
                                    <ReactApexChart options={chartOptions.options} series={chartOptions.series} type="area" height={350} />
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={3}>
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header>
                                <h3>주식 거래</h3>
                            </Card.Header>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </>
    );
}

export default Trading;
