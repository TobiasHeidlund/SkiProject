import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import { Line } from "react-chartjs-2";
import { MapContainer, TileLayer, Polyline, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import "./RunDetails.css"
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

const backendUrl = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

interface RunData {
  lat: number;
  lon: number;
  alt: number;
  speed: number;
  time: number;
  error: number;
  id: number;
}

const RunDetails: React.FC = () => {
  const { runId } = useParams<{ runId: string }>(); // Extract runId from the URL
  const [runData, setRunData] = useState<RunData[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRunData = async (id: string) => {
    try {
      setLoading(true);
      const response = await axios.get(`${backendUrl}/run/${id}`);
      setRunData(response.data); // Assuming the backend returns a list of RunData
      console.log(response.data);
      setError(null);
    } catch (err) {
      console.error("Error fetching run data:", err);
      setError("Failed to fetch run data. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (runId) {
      fetchRunData(runId);
    }
  }, [runId]);

  if (loading) {
    return <p>Loading run details...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  if (runData.length === 0) {
    return <p>No data available for this run.</p>;
  }

  // Prepare data for the chart
  const chartData = {
    labels: runData.map((data) => new Date(data.time).toLocaleTimeString()), // Time as labels
    datasets: [
      {
        label: "Altitude (m)",
        data: runData.map((data) => data.alt),
        borderColor: "rgba(75, 192, 192, 1)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        tension: 0.4,
      },
      {
        label: "Speed (m/s)",
        data: runData.map((data) => data.speed),
        borderColor: "rgba(255, 99, 132, 1)",
        backgroundColor: "rgba(255, 99, 132, 0.2)",
        tension: 0.4,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: {
        position: "top" as const,
      },
      title: {
        display: true,
        text: "Run Data (Altitude and Speed over Time)",
      },
    },
    maintainAspectRatio: false
  };

  const positions = runData.map((data) => [data.lat, data.lon]);

  return (
    <div className="run-details-container">
      <h1>Run Details</h1>
      <div>
        <Line data={chartData} options={chartOptions} height={300}/>
      </div>
      <MapContainer
        center={positions[0]}
        zoom={13}
        style={{ height: "400px", width: "100%", marginTop: "20px" }}
      >
        <TileLayer
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        <Polyline positions={positions}  />
        
      </MapContainer>
      <table
        border={1}
        style={{ width: "100%", borderCollapse: "collapse", marginTop: "20px" }}
      >
        <thead>
          <tr>
            <th>Lat</th>
            <th>Lon</th>
            <th>Altitude (m)</th>
            <th>Speed (m/s)</th>
            <th>Time</th>
          </tr>
        </thead>
        <tbody>
          {runData.map((data) => (
            <tr key={data.id}>
              <td>{data.lat.toFixed(3)}</td>
              <td>{data.lon.toFixed(3)}</td>
              <td>{data.alt.toFixed(2)}</td>
              <td>{data.speed.toFixed(2)}</td>
              <td>{new Date(data.time).toLocaleString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default RunDetails;