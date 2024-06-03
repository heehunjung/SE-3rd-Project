import React, { useEffect, useState } from 'react';
import {Nav, Container, Form, Button, Row, Col, Card, Badge, InputGroup} from 'react-bootstrap';
import Navbar from 'react-bootstrap/Navbar';
import {useLocation, useNavigate, useParams} from "react-router-dom";
import ReactApexChart from 'react-apexcharts';
import '../App.css';
import './Board.css'; // 추가된 스타일 시트

const Trading = () => {
    const { id } = useParams();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const stockId = queryParams.get('stockId') ?? '1';  // 기본값을 1로 설정
    const [stock, setStock] = useState(null);
    const [stockPrice, setStockPrice] = useState([]);
    const [error, setError] = useState(null);
    const [change, setChange] = useState(null);
    const [stockName, setStockName] = useState(null);
    const navigate = useNavigate();
    const [userData, setUserData] = useState(null);
    const [sellBuy, setSellBuy] = useState({
        stockName:'',
        stockQuantity:'',
        stockId:stockId
    });
    const [totalAmount,setTotalAmount] = useState(null);
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
    // id를 통해 멤버 객체를 가져옴
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
    //주식의 상승률 , 하락률을 계산하는 메소드
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
    //입력받은 주식이름을 객체에 저장
    const handleChange = (e) =>{
        setStockName(e.target.value);
    }
    //입력받은 매도/매수 정보를 객체에 저장
    const handleChangeBuySell = (e) =>{
        if(!stock){
            alert('주식 정보를 가져오고있습니다. 잠시만 기다려주세요.');
            return;
        }
        setSellBuy({
            ...setSellBuy,
            [e.target.name]: e.target.value,
            stockId: stockId,
            stockName: stock.stockName
            }
        );
        if (e.target.name=== 'stockQuantity' && stock) {
            setTotalAmount(e.target.value * stock.currentPrice);
        }
    }
    //검색 주식을 확인하는 메소드
    const handleSubmit = (e) => {
        e.preventDefault();
        if (!stockName) {
            alert('검색 내용을 가져오는 중입니다. 잠시만 기다려주세요.');
            return;
        }
        fetch(`http://localhost:8080/stockData/name/${stockName}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json; charset=utf-8',
            }
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error('주식 이름을 다시 입력해주세요.');
                }
                return res.json();
            })
            .then(data => {
                navigate(`/trading/${id}/?stockId=${data.id}`);
            })
            .catch(error => {
                alert(error.message); // 오류 메시지를 출력
                console.log(error.message); // 오류 메시지를 콘솔에 출력
            });
    }
    //매도 매수 api 요청하는 메소드
    const onSellBuySubmit = (e,action) => {
        e.preventDefault();
        if(!sellBuy) {
            alert('입력한 주식 정보를 저장 중입니다. 잠시만 기다려주세요.');
            return;
        }
        const url = action === 'sell' ? `http://localhost:8080/sell/${id}` : `http://localhost:8080/buy/${id}`;
        fetch(url,{
            method:'POST',
            headers:{
                'Content-Type': 'application/json; charset=utf-8',
            },
            body:JSON.stringify(sellBuy)
        })
            .then(res => {
                if (res.status === 200 || res.status === 201) {
                    return res.text();
                } else {
                    return res.text().then(text => Promise.reject(text));
                }
            })
            .then(data=>{
                console.log(data);
                fetch(`http://localhost:8080/memberInfo/${id}`)
                    .then(response => response.json())
                    .then(data => {
                        setUserData(data);
                        navigate(`/trading/${id}/?stockId=${stockId}`);
                    });
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error);
            });
    }
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
                    {userData && userData.role === 'ADMIN' ? (
                        <Navbar.Brand href={`/Home/${id}`}>KW 거래소📉 관리자 모드</Navbar.Brand>
                    ) : (
                        <Navbar.Brand href={`/Home/${id}`}>KW 거래소📉</Navbar.Brand>
                    )}
                    <Nav className="ml-auto">
                        <Nav.Link href={`/Home/${id}`}>홈 화면</Nav.Link>
                        <Nav.Link href={`/Trading/${id}`}>주식 구매</Nav.Link>
                        <Nav.Link href={`/Board/${id}`}>커뮤니티</Nav.Link>
                        <Nav.Link href={`/MyInfo/${id}`}>내 정보</Nav.Link>
                        <Nav.Link href={`/Post/${id}`}>게시글 작성</Nav.Link>
                        <Nav.Link href={'/Login'}>로그아웃</Nav.Link>
                    </Nav>
                </Container>
            </Navbar>
            <br />
            <Container>
                <Row>
                    <Col>
                        <div className="form-container">
                            <Form onSubmit={handleSubmit}> {/* 폼 제출 핸들러 설정 */}
                                <InputGroup>
                                    <Form.Control
                                        size="lg"
                                        type="text"
                                        placeholder="주식 이름"
                                        value={stockName}
                                        onChange={handleChange}
                                    />
                                    <Button className="btn-icon2" type="submit">🔍</Button>
                                </InputGroup>
                            </Form>
                        </div>
                    </Col>
                </Row>
                <Row>
                    <Col md={9}>
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Title>
                                <Card.Header>
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
                        <Card className="mb-4 shadow-sm card-custom">  {/* 둥근 모서리를 위해 Card로 감쌌습니다 */}
                            <Card.Header>
                                <h4>보유 잔고🏦 {userData && userData.balance}원</h4>
                            </Card.Header>
                        </Card>                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header>
                                <h4 style={{ textAlign: 'center', margin: '10px'}}>주식 거래</h4>
                                <Form>
                                    주식 이름
                                    <Form.Control
                                        size="lg"
                                        type="text"
                                        placeholder="주식 이름을 입력해주세요"
                                        name="stockName" // 추가
                                        value={stock && stock.stockName}
                                        readOnly
                                        className="small-placeholder"
                                    />
                                    매도/매수 수량
                                    <Form.Control
                                        size="lg"
                                        type="text"
                                        placeholder="매도/매수 수량을 입력해주세요"
                                        name="stockQuantity" // 추가
                                        value={sellBuy.stockQuantity}
                                        onChange={handleChangeBuySell}
                                        className="small-placeholder"
                                    />
                                    총 금액(원)
                                    <Form.Control
                                        size="lg"
                                        type="text"
                                        placeholder="총 금액"
                                        className="small-placeholder"
                                        value={totalAmount}
                                        readOnly
                                    />
                                    <div className="button-group" style={{ margin: '10px' }}>
                                        <Button variant="danger" className="trade-button" onClick={(e) => onSellBuySubmit(e, 'sell')}>매도</Button>
                                        <Button variant="primary" className="trade-button" onClick={(e) => onSellBuySubmit(e, 'buy')}>매수</Button>
                                    </div>
                                </Form>

                            </Card.Header>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </>
    );
}

export default Trading;
