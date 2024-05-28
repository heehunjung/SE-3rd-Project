import React, { useEffect, useState } from 'react';

const StockChart = () => {
    const [stockData, setStockData] = useState([]);

    useEffect(() => {
        const fetchStockData = async () => {
            try {
                const response = await fetch('http://localhost:8080/stock/kospi');
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const text = await response.text();
                const parser = new DOMParser();
                const doc = parser.parseFromString(text, 'text/html');
                const rows = Array.from(doc.querySelectorAll('table.type_1 tr'));

                const stockData = rows.map(row => {
                    const columns = row.querySelectorAll('td');
                    if (columns.length > 1) {
                        const date = columns[0].innerText.trim();
                        const closePrice = parseFloat(columns[1].innerText.replace(/,/g, ''));
                        return { date, closePrice };
                    }
                }).filter(Boolean);

                setStockData(stockData);
                drawChart(stockData);
            } catch (error) {
                console.error('Error fetching stock data:', error);
            }
        };

        const drawChart = (data) => {
            const canvas = document.getElementById('stockChart');
            if (canvas) {
                const ctx = canvas.getContext('2d');
                const labels = data.map(item => item.date).reverse();
                const prices = data.map(item => item.closePrice).reverse();

                ctx.clearRect(0, 0, canvas.width, canvas.height);

                ctx.beginPath();
                ctx.moveTo(0, canvas.height - prices[0]);
                prices.forEach((price, index) => {
                    const x = (canvas.width / prices.length) * index;
                    const y = canvas.height - price;
                    ctx.lineTo(x, y);
                });
                ctx.stroke();
            } else {
                console.error('Canvas element is null');
            }
        };

        fetchStockData();
    }, []);

    return (
        <canvas id="stockChart" width="400" height="200"></canvas>
    );
};

export default StockChart;
