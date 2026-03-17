import React, { useState, useEffect, useRef } from "react";
import { useAuth } from "../context/AuthContext";
import { useWebSocket } from "../hooks/useWebSocket";
import api from "../api/axios";

import Navbar from "../components/Navbar";
import StockCard from "../components/StockCard";
import OrderForm from "../components/OrderForm";
import MarketStatus from "../components/MarketStatus";
import CandlestickChart from "../components/CandlestickChart";

import {
  TrendingUp,
  TrendingDown,
  Wallet,
  BarChart2,
  RefreshCw,
  X,
} from "lucide-react";

import { s, sc, ml } from "../styles/dashboardStyles";

export default function Dashboard() {
  const { user } = useAuth();
  const { prices, connected, flashMap } = useWebSocket();

  const [searchQuery, setSearchQuery] = useState("");
  const [stocks, setStocks] = useState([]);
  const [portfolio, setPortfolio] = useState(null);
  const [marketStatus, setMarketStatus] = useState(null);

  const [loading, setLoading] = useState(true);

  const [selectedStock, setSelectedStock] = useState(null);
  const [showOrderForm, setShowOrderForm] = useState(false);
  const [orderSide, setOrderSide] = useState("BUY");

  const [refreshing, setRefreshing] = useState(false);

  // ✅ CHART STATES
  const [selectedTicker, setSelectedTicker] = useState("");
  const [allCandles, setAllCandles] = useState({});
  const candles = allCandles[selectedTicker] || [];

  const priceRef = useRef(null);
  const tickCountRef = useRef({});

  useEffect(() => {
    fetchAll();
  }, []);

  useEffect(() => {
    if (stocks.length > 0 && !selectedTicker) {
      setSelectedTicker(stocks[0].ticker);
    }
  }, [stocks]);

  useEffect(() => {
    if (!selectedTicker) return;

    const loadCandles = async () => {
      try {
        const res = await api.get(`/candles?ticker=${selectedTicker}`);

        setAllCandles((prev) => {
          const candles = res.data;

          if (candles.length > 0) {
            const last = candles[candles.length - 1];
            last.time = Math.floor(last.time / 5) * 5;
          }

          return {
            ...prev,
            [selectedTicker]: candles,
          };
        });
      } catch (err) {
        console.error("Failed to load candles", err);
      }
    };

    loadCandles();
  }, [selectedTicker]);

  // ❌ REMOVED WRONG RESET

  useEffect(() => {
    priceRef.current = prices[selectedTicker];
  }, [prices, selectedTicker]);

  const fetchAll = async () => {
    try {
      const [stocksRes, portfolioRes, marketRes] = await Promise.all([
        api.get("/stocks/all"),
        api.get("/portfolio/me"),
        api.get("/market/status"),
      ]);

      setStocks(stocksRes.data);
      setPortfolio(portfolioRes.data);
      setMarketStatus(marketRes.data);
    } catch (err) {
      console.error("Failed to fetch dashboard data", err);
    } finally {
      setLoading(false);
    }
  };

  const handleRefresh = async () => {
    setRefreshing(true);
    await fetchAll();
    setTimeout(() => setRefreshing(false), 600);
  };

  const openOrderForm = (stock, side) => {
    setSelectedStock(stock);
    setOrderSide(side);
    setShowOrderForm(true);
  };

  const handleOrderSuccess = () => {
    setShowOrderForm(false);
    fetchAll();
  };

  const enrichedStocks = stocks.map((s) => ({
    ...s,
    currentPrice: prices[s.ticker] ?? s.currentPrice,
  }));

  // ✅ EVENT-DRIVEN CANDLE ENGINE (FINAL FIX)
  useEffect(() => {
    const livePrice = prices[selectedTicker];
    if (!livePrice || !selectedTicker) return;

    const ticker = selectedTicker;

    if (!tickCountRef.current[ticker]) {
      tickCountRef.current[ticker] = 0;
    }

    tickCountRef.current[ticker]++;

    // ❌ IGNORE FIRST 2–3 TICKS
    if (tickCountRef.current[ticker] < 3) {
      return;
    }

    setAllCandles((prev) => {
      const prevCandles = prev[selectedTicker] || [];

      // 🚨 prevent overriding initial API load instantly
      if (prevCandles.length === 0) return prev;

      // ✅ 5 SECOND BUCKET
      const interval = 30; // 🔥 MATCH BACKEND
      const now = Math.floor(Date.now() / 1000);
      const bucketTime = Math.floor(now / interval) * interval;

      const last = prevCandles[prevCandles.length - 1];

      if (!last) {
        return {
          ...prev,
          [selectedTicker]: [
            {
              time: bucketTime,
              open: livePrice,
              high: livePrice,
              low: livePrice,
              close: livePrice,
            },
          ],
        };
      }

      // 🔥 FIXED CONDITION
      if (bucketTime === last.time) {
        const updated = {
          ...last,
          high: Math.max(last.high, livePrice),
          low: Math.min(last.low, livePrice),
          close: livePrice,
        };

        const arr = [...prevCandles];
        arr[arr.length - 1] = updated;

        return {
          ...prev,
          [selectedTicker]: arr,
        };
      }

      // 👉 NEW CANDLE
      return {
        ...prev,
        [selectedTicker]: [
          ...prevCandles,
          {
            time: bucketTime,
            open: livePrice,
            high: livePrice,
            low: livePrice,
            close: livePrice,
          },
        ].slice(-50),
      };
    });
  }, [prices, selectedTicker]);

  const gainers = enrichedStocks
    .filter((s) => s.priceChangeAmount > 0)
    .sort((a, b) => b.priceChangePercent - a.priceChangePercent)
    .slice(0, 3);

  const losers = enrichedStocks
    .filter((s) => s.priceChangeAmount < 0)
    .sort((a, b) => a.priceChangePercent - b.priceChangePercent)
    .slice(0, 3);

  const filteredStocks = enrichedStocks.filter(
    (s) =>
      s.ticker.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.companyName.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (loading) return <DashboardSkeleton />;

  return (
    <div style={s.root}>
      <Navbar />

      <div style={s.page}>
        {/* HEADER */}
        <div style={s.header}>
          <div>
            <h1 style={s.title}>
              Good {getGreeting()},
              <span style={{ color: "var(--accent-cyan)" }}>
                {" "}
                {user?.username}
              </span>
            </h1>
            <p style={s.subtitle}>
              Here's what's happening in the market today
            </p>
          </div>

          <div style={s.headerRight}>
            <MarketStatus status={marketStatus} />

            <button
              onClick={handleRefresh}
              className="btn btn-ghost"
              style={{ gap: 6 }}
            >
              <RefreshCw
                size={14}
                style={{
                  animation: refreshing ? "spin 0.6s linear infinite" : "none",
                }}
              />
              Refresh
            </button>
          </div>
        </div>

        {/* WS STATUS */}
        <div style={s.wsStatus}>
          <div
            style={{
              ...s.wsDot,
              background: connected ? "var(--gain)" : "var(--loss)",
            }}
          />
          <span style={s.wsText}>
            {connected ? "Live prices connected" : "Connecting..."}
          </span>
        </div>

        {/* SUMMARY */}
        {portfolio && (
          <div style={s.summaryStrip} className="stagger">
            <SummaryCard
              label="Available Balance"
              value={`₹${portfolio.availableBalance?.toLocaleString("en-IN")}`}
              icon={<Wallet size={18} />}
              color="var(--accent-cyan)"
            />

            <SummaryCard
              label="Portfolio Value"
              value={`₹${portfolio.currentMarketValue?.toLocaleString(
                "en-IN"
              )}`}
              icon={<BarChart2 size={18} />}
              color="var(--accent-amber)"
            />

            <SummaryCard
              label="Total P&L"
              value={`₹${portfolio.totalProfitLoss?.toLocaleString("en-IN")}`}
              icon={
                portfolio.totalProfitLoss >= 0 ? (
                  <TrendingUp size={18} />
                ) : (
                  <TrendingDown size={18} />
                )
              }
              color={
                portfolio.totalProfitLoss >= 0 ? "var(--gain)" : "var(--loss)"
              }
              sub={`${portfolio.totalProfitLossPercentage?.toFixed(2)}%`}
            />
          </div>
        )}

        {/* GAINERS / LOSERS */}
        <div style={s.glRow}>
          <MiniList title="Top Gainers" items={gainers} dir="up" />
          <MiniList title="Top Losers" items={losers} dir="down" />
        </div>

        {/* 🔥 CHART SECTION */}
        <div style={{ marginBottom: 24 }}>
          <div
            style={{
              marginBottom: 12,
              display: "flex",
              gap: 10,
              alignItems: "center",
            }}
          >
            <span style={{ fontSize: 12, color: "var(--text-muted)" }}>
              Select Stock:
            </span>

            <select
              value={selectedTicker}
              onChange={(e) => setSelectedTicker(e.target.value)}
              className="input"
              style={{ width: 180 }}
            >
              {stocks.map((s) => (
                <option key={s.ticker} value={s.ticker}>
                  {s.ticker}
                </option>
              ))}
            </select>
          </div>

          <CandlestickChart data={[...candles]} />
        </div>

        {/* STOCK LIST */}
        <div style={s.stockGrid}>
          {filteredStocks.map((stock) => (
            <StockCard
              key={stock.ticker}
              stock={stock}
              flash={flashMap[stock.ticker]}
              onBuy={() => openOrderForm(stock, "BUY")}
              onSell={() => openOrderForm(stock, "SELL")}
              isMarketOpen={marketStatus?.isOpen}
            />
          ))}
        </div>
      </div>

      {/* ORDER MODAL */}
      {showOrderForm && (
        <div style={s.modalOverlay}>
          <div style={s.modalCard}>
            <div style={s.modalHeader}>
              <h3 style={s.modalTitle}>
                {orderSide === "BUY" ? "Buy" : "Sell"}
              </h3>
              <button onClick={() => setShowOrderForm(false)}>
                <X size={18} />
              </button>
            </div>

            <OrderForm
              stock={selectedStock}
              initialSide={orderSide}
              onSuccess={handleOrderSuccess}
            />
          </div>
        </div>
      )}
    </div>
  );
}

function SummaryCard({ label, value, icon, color, sub, warn }) {
  return (
    <div style={sc.card} className="card">
      <div style={sc.iconRow}>
        <div style={{ ...sc.icon, color }}>{icon}</div>
      </div>

      <div style={{ ...sc.value, color }}>{value}</div>

      {sub && <div style={sc.sub}>{sub}</div>}

      <div style={sc.label}>{label}</div>
    </div>
  );
}

function MiniList({ title, items, dir }) {
  return (
    <div style={ml.card} className="card">
      <div style={ml.header}>
        <span style={ml.title}>{title}</span>
      </div>

      {items.map((s) => (
        <div key={s.ticker} style={ml.row}>
          <span style={ml.ticker}>{s.ticker}</span>

          <span style={ml.price}>
            ₹{parseFloat(s.currentPrice).toLocaleString("en-IN")}
          </span>

          <span
            style={{
              ...ml.pct,
              color: dir === "up" ? "var(--gain)" : "var(--loss)",
            }}
          >
            {Math.abs(s.priceChangePercent).toFixed(2)}%
          </span>
        </div>
      ))}
    </div>
  );
}

function DashboardSkeleton() {
  return (
    <div style={{ padding: 24 }}>
      <div
        style={{
          height: 40,
          width: 300,
          marginBottom: 24,
        }}
        className="skeleton"
      />
    </div>
  );
}

function getGreeting() {
  const h = new Date().getHours();

  if (h < 12) return "morning";
  if (h < 17) return "afternoon";
  return "evening";
}
