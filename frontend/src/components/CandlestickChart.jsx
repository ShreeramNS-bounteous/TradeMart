import React, { useEffect, useRef } from "react";
import { createChart, CandlestickSeries } from "lightweight-charts";

export default function CandlestickChart({ data }) {
  const chartContainerRef = useRef();
  const chartRef = useRef();
  const seriesRef = useRef();
  const prevLengthRef = useRef(0);

  // ✅ CREATE CHART
  useEffect(() => {
    const chart = createChart(chartContainerRef.current, {
      width: chartContainerRef.current.clientWidth,
      height: 350,
      layout: {
        background: { color: "#0f172a" },
        textColor: "#cbd5f5",
      },
      grid: {
        vertLines: { color: "rgba(255,255,255,0.05)" },
        horzLines: { color: "rgba(255,255,255,0.05)" },
      },
      handleScroll: {
        mouseWheel: true,
        pressedMouseMove: true,
        horzTouchDrag: true,
        vertTouchDrag: true,
      },
      handleScale: {
        axisPressedMouseMove: true,
        mouseWheel: true,
        pinch: true,
      },
    });

    const candleSeries = chart.addSeries(CandlestickSeries, {
      upColor: "#22c55e",
      downColor: "#ef4444",
      borderVisible: false,
      wickUpColor: "#22c55e",
      wickDownColor: "#ef4444",
    });

    chartRef.current = chart;
    seriesRef.current = candleSeries;

    return () => chart.remove();
  }, []);

  // ✅ RESIZE FIX (MAIN BUG FIX 🔥)
  useEffect(() => {
    const handleResize = () => {
      if (chartRef.current && chartContainerRef.current) {
        chartRef.current.applyOptions({
          width: chartContainerRef.current.clientWidth,
        });
      }
    };

    window.addEventListener("resize", handleResize);

    // 🔥 FIX INITIAL RENDER ISSUE
    setTimeout(handleResize, 200);

    return () => window.removeEventListener("resize", handleResize);
  }, []);

  useEffect(() => {
    if (!seriesRef.current || data.length === 0) return;
  
    const safeData = data.filter(
      (c, i, arr) => i === 0 || c.time > arr[i - 1].time
    );
  
    if (safeData.length === 0) return;
  
    const newLastTime = safeData[safeData.length - 1].time;
  
    // 🔥 KEY FIX: detect fresh dataset (initial load / stock switch)
    const isNewDataset =
      prevLengthRef.current === 0 ||
      safeData.length < prevLengthRef.current;
  
    if (isNewDataset) {
      // 💥 FULL RESET (IMPORTANT)
      seriesRef.current.setData(safeData);
    } else {
      // 🔄 LIVE UPDATE
      seriesRef.current.update(safeData[safeData.length - 1]);
    }
  
    prevLengthRef.current = safeData.length;
  
    if (chartRef.current) {
      chartRef.current.timeScale().fitContent();
    }
  
  }, [data]);

  return (
    <div ref={chartContainerRef} style={{ width: "100%", height: 350 }} />
  );
}