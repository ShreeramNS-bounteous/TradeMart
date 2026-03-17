import React, { useEffect, useRef } from "react";
import { createChart, CandlestickSeries } from "lightweight-charts";

export default function CandlestickChart({ data }) {
  const chartContainerRef = useRef();
  const chartRef = useRef();
    const seriesRef = useRef();
    const prevLengthRef = useRef(0)

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

      // ✅ ADD THIS
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

  useEffect(() => {

    if (!seriesRef.current || data.length === 0) return
  
    const safeData = data.filter(
      (c, i, arr) => i === 0 || c.time > arr[i - 1].time
    )
  
    if (safeData.length === 0) return
  
    const lastChartTime = seriesRef.current._lastTime || 0
    const newLastTime = safeData[safeData.length - 1].time
  
    // ✅ IMPORTANT LOGIC
    if (newLastTime <= lastChartTime) {
      // 🔁 fallback → FULL RESET
      seriesRef.current.setData(safeData)
    } else if (prevLengthRef.current === 0) {
      seriesRef.current.setData(safeData)
    } else if (safeData.length < prevLengthRef.current) {
      seriesRef.current.setData(safeData)
    } else {
      seriesRef.current.update(safeData[safeData.length - 1])
    }
  
    // store last time manually
    seriesRef.current._lastTime = newLastTime
    prevLengthRef.current = safeData.length
  
  }, [data])
    
    
    
  return <div ref={chartContainerRef} style={{ width: "100%", height: 350 }} />;
}
