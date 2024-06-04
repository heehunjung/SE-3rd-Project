import React, { useEffect, useState } from 'react';
import {Nav, Container, Form, Button, Row, Col, Card, Badge, InputGroup} from 'react-bootstrap';
import Navbar from 'react-bootstrap/Navbar';
import {useLocation, useNavigate, useParams} from "react-router-dom";
import ReactApexChart from 'react-apexcharts';
import '../App.css';
import './Board.css';

const Trading = () => {
    const { id } = useParams();
    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const stockId = queryParams.get('stockId') ?? '1';
    const [stock, setStock] = useState(null);
    const [stockPrice, setStockPrice] = useState([]);
    const [error, setError] = useState(null);
    const [change, setChange] = useState(null);
    const [stockName, setStockName] = useState(null);
    const navigate = useNavigate();
    const [stockYesterday, setStockYesterday] = useState(null);
    const [userData, setUserData] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const [sellBuy, setSellBuy] = useState({ stockName: '', stockQuantity: '', stockId: stockId });
    const [totalAmount, setTotalAmount] = useState(null);
    const [memberStock, setMemberStock] = useState(null);

    useEffect(() => {
        fetchStockData();
        fetchStockPrice();
        fetchMemberInfo();
        fetchMemberStockData();
    }, [stockId, id]);

    useEffect(() => {
        fetch(`http://localhost:8080/stockData/yesterDay/${stockId}`)
            .then(res => {
                if (!res.ok) {
                    return res.text().then(text => { throw new Error(text); });
                }
                return res.json();
            })
            .then(data => {
                setStockYesterday(data);
            })
            .catch(error => {
                setError(error.message);
                alert(error.message);
            });
    }, [stockId]);

    const fetchStockData = async () => {
        try {
            const res = await fetch(`http://localhost:8080/stockData/${stockId}`);
            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            setStock(data);
            fetchUpAndDown(data.id);
        } catch (error) {
            setError(error.message);
            alert(error.message);
        }
    };

    const fetchStockPrice = async () => {
        try {
            const res = await fetch(`http://localhost:8080/stockPrice/${stockId}`);
            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            setStockPrice(data.map(item => ({ x: new Date(item.date), y: item.closingPrice })));
        } catch (error) {
            setError(error.message);
            alert(error.message);
        }
    };

    const fetchMemberInfo = async () => {
        try {
            const res = await fetch(`http://localhost:8080/memberInfo/${id}`);
            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            setUserData(data);
        } catch (error) {
            setError(error.message);
            alert(error.message);
        }
    };

    const fetchMemberStockData = async () => {
        try {
            const res = await fetch(`http://localhost:8080/memberStock/${id}`);
            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            fetchStockInfo(data);
        } catch (error) {
            setError(error.message);
            alert(error.message);
        }
    };

    const fetchStockInfo = async (memberStockList) => {
        try {
            setIsLoading(true);
            const promises = memberStockList.map(stockItem =>
                fetch(`http://localhost:8080/stockData/${stockItem.stock.id}`)
                    .then(res => {
                        if (!res.ok) return res.text().then(text => { throw new Error(text); });
                        return res.json();
                    })
                    .then(stockData => ({
                        ...stockItem,
                        stockName: stockData.stockName,
                        currentPrice: stockData.currentPrice
                    }))
            );

            const stockInfoList = await Promise.all(promises);
            setMemberStock(stockInfoList);
            setIsLoading(false);
        } catch (error) {
            setError(error.message);
            alert(error.message);
            setIsLoading(false);
        }
    };

    const fetchUpAndDown = async (stockId) => {
        try {
            const res = await fetch(`http://localhost:8080/changes/${stockId}`);
            if (!res.ok) throw new Error(await res.text());
            const data = await res.json();
            setChange(data);
        } catch (error) {
            setError(error.message);
            alert(error.message);
        }
    };

    const handleChange = (e) => {
        setStockName(e.target.value);
    };

    const handleChangeBuySell = (e) => {
        if (!stock) {
            alert('주식 정보를 가져오고 있습니다. 잠시만 기다려주세요.');
            return;
        }
        setSellBuy({
            ...sellBuy,
            [e.target.name]: e.target.value,
            stockId: stockId,
            stockName: stock.stockName
        });
        if (e.target.name === 'stockQuantity' && stock) {
            setTotalAmount(e.target.value * stock.currentPrice);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!stockName) {
            alert('검색 내용을 가져오는 중입니다. 잠시만 기다려주세요.');
            return;
        }
        try {
            const res = await fetch(`http://localhost:8080/stockData/name/${stockName}`);
            if (!res.ok) throw new Error('주식 이름을 다시 입력해주세요.');
            const data = await res.json();
            navigate(`/trading/${id}/?stockId=${data.id}`);
        } catch (error) {
            alert(error.message);
        }
    };

    const onSellBuySubmit = async (e, action) => {
        e.preventDefault();
        if (!sellBuy) {
            alert('입력한 주식 정보를 저장 중입니다. 잠시만 기다려주세요.');
            return;
        }
        const url = action === 'sell' ? `http://localhost:8080/sell/${id}` : `http://localhost:8080/buy/${id}`;
        try {
            const res = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json; charset=utf-8' },
                body: JSON.stringify(sellBuy)
            });
            if (res.status !== 200 && res.status !== 201) throw new Error(await res.text());
            const data = await res.text();
            alert(data);
            const updatedMemberStock = [...memberStock];
            const existingStock = updatedMemberStock.find(s => s.stock.id === stockId);
            if (existingStock) {
                existingStock.quantity = action === 'buy'
                    ? existingStock.quantity + Number(sellBuy.stockQuantity)
                    : existingStock.quantity - Number(sellBuy.stockQuantity);
            } else {
                updatedMemberStock.push({
                    stock: { id: stockId, stockName: stock.stockName, currentPrice: stock.currentPrice },
                    quantity: Number(sellBuy.stockQuantity),
                    createdAt: new Date()
                });
            }
            setMemberStock(updatedMemberStock);
            await fetchMemberInfo();
            await fetchMemberStockData();
        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    };

    const chartOptions = {
        series: [{ name: '종가', data: stockPrice }],
        options: {
            chart: {
                type: 'area',
                stacked: false,
                height: 400,
                zoom: { type: 'x', enabled: true, autoScaleYaxis: true },
                toolbar: { autoSelected: 'zoom' }
            },
            dataLabels: { enabled: false },
            markers: { size: 0 },
            fill: {
                type: 'gradient',
                gradient: { shadeIntensity: 1, inverseColors: false, opacityFrom: 0.5, opacityTo: 0, stops: [0, 90, 100] }
            },
            yaxis: {
                labels: { formatter: val => val.toFixed(2) },
                title: { text: '가격' }
            },
            xaxis: {
                type: 'datetime',
                labels: {
                    datetimeFormatter: { year: 'yyyy년', month: 'MM월', day: 'dd일', hour: 'HH시', minute: 'mm분' }
                }
            },
            tooltip: {
                shared: false,
                y: { formatter: val => val.toFixed(2) }
            },
            theme: { mode: 'dark' }
        },
    };

    return (
        <>
            <Navbar bg="dark" data-bs-theme="dark">
                <Container>
                    <Navbar.Brand href={`/Home/${id}`}>{userData?.role === 'ADMIN' ? 'KW 거래소📉 관리자 모드' : 'KW 거래소📉'}</Navbar.Brand>
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
            <Container>
                <Row>
                    <Col>
                        <div className="form-container">
                            <Form onSubmit={handleSubmit}>
                                <InputGroup>
                                    <Form.Control size="lg" type="text" placeholder="주식 이름" value={stockName} onChange={handleChange} />
                                    <Button className="btn-icon2" type="submit">🔍</Button>
                                </InputGroup>
                            </Form>
                        </div>
                    </Col>
                </Row>
                <Row>
                    <Col md={8}>
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Title>
                                <Card.Header>
                                    {!stock && !error && <p>데이터를 불러오는 중...</p>}
                                    {stock && (
                                        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                            <div>
                                                <h3><strong>{stock.stockName}</strong></h3>
                                                <strong>{stock.currentPrice?.toLocaleString() ?? 'N/A'}원</strong>
                                                <Badge bg={change > 0 ? 'danger' : 'primary'} style={{ marginLeft: '10px' }}>
                                                    {change !== null ? (change > 0 ? '📈' : '📉') + (change * 100).toFixed(2) : 'N/A'}%
                                                </Badge>
                                            </div>
                                            <div style={{ textAlign: 'left', fontSize: '0.8em' }}>
                                                <strong>
                                                    <div>상한가 {stockYesterday?.highPrice?.toLocaleString() ?? 'N/A'}원</div>
                                                    <div>하한가 {stockYesterday?.lowPrice?.toLocaleString() ?? 'N/A'}원</div>
                                                    <div>거래량 {stockYesterday?.volume?.toLocaleString() ?? 'N/A'}</div>
                                                </strong>
                                            </div>
                                        </div>
                                    )}
                                </Card.Header>
                            </Card.Title>
                            <Card.Body>
                                <div style={{ height: 370 }}>
                                    <ReactApexChart options={chartOptions.options} series={chartOptions.series} type="area" height={350} />
                                </div>
                            </Card.Body>
                        </Card>
                        <Card>
                            <Card.Header>거래 기록</Card.Header>
                        </Card>

                    </Col>
                    <Col md={4}>
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header>
                                <strong>보유 잔고🏦 {userData?.balance?.toLocaleString() ?? 'N/A'}원</strong>
                            </Card.Header>
                        </Card>
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header>
                                <h4 style={{ textAlign: 'center', margin: '10px'}}>주식 거래</h4>
                                <Form>
                                    주식 이름
                                    <Form.Control
                                        size="lg"
                                        type="text"
                                        placeholder="주식 이름을 입력해주세요"
                                        name="stockName"
                                        value={stock && stock.stockName}
                                        readOnly
                                        className="small-placeholder"
                                    />
                                    매도/매수 수량
                                    <Form.Control
                                        size="lg"
                                        type="text"
                                        placeholder="매도/매수 수량을 입력해주세요"
                                        name="stockQuantity"
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
                        <Card className="mb-4 shadow-sm card-custom">
                            <Card.Header>
                                <h4 style={{textAlign:"center"}}><strong>내 포트폴리오📖</strong></h4>
                            </Card.Header>
                            <Card.Body>
                                <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 'bold', marginBottom: '10px' }}>
                                    <div style={{ flex: 1, textAlign: 'left' }}>주식 명</div>
                                    <div style={{ flex: 1, textAlign: 'left' }}>개수</div>
                                    <div style={{ flex: 1, textAlign: 'left' }}>매수 금액</div>
                                </div>
                                {isLoading ? (
                                    <p>데이터를 불러오는 중...</p>
                                ) : memberStock && memberStock.length > 0 ? (
                                    <ul style={{ listStyleType: 'none', padding: 0 }}>
                                        {memberStock.map((stock, index) => (
                                            <li key={index} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '5px' }}>
                                                <div style={{ flex: 1, textAlign: 'left' }}>{stock.stockName}</div>
                                                <div style={{ flex: 1, textAlign: 'left' }}>{stock.quantity}주</div>
                                                <div style={{ flex: 1, textAlign: 'left' }}>{stock.currentPrice?.toLocaleString() ?? 'N/A'}원</div>
                                            </li>
                                        ))}
                                    </ul>
                                ) : (
                                    <p>보유한 주식이 없습니다.</p>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>

            </Container>
        </>
    );
};

export default Trading;
