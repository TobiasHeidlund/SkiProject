import React, { useEffect, useState } from "react";
import axios from "axios";
import "./RunSummary.css"

const backendUrl = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

interface Run {
  id: number;
  start_time: number;
  end_time: number;
  total_altitude: number;
  avg_speed: number;
  top_speed: number;
  total_distance: number;
}

interface RunProps {
  date: string; // The date parameter passed to the component
}

const RunSummary: React.FC<RunProps> = ({ date }) => {
  const [runs, setRuns] = useState<Run[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchRuns = async (date: string) => {
    try {
      setLoading(true);
      const response = await axios.get(`${backendUrl}/run`, {
        params: { date },
      });
      setRuns(response.data); // Assuming the backend returns an array of runs
      setError(null);
    } catch (err) {
      console.error("Error fetching runs:", err);
      setError("Failed to fetch runs. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (date) {
      fetchRuns(date);
    }
  }, [date]);

  if (loading) {
    return <p>Loading runs...</p>;
  }

  if (error) {
    return <p>{error}</p>;
  }

  if (runs.length === 0) {
    return <p>No runs available for the selected date.</p>;
  }

  const handleRowClick = (runId: number) => {
    window.location.href = `/run/${runId}`;
  };

  return (
    <div className="run-summary-container">
      <h2>Runs for {date}</h2>
      <table border={1}>
        <thead>
          <tr>
            <th>Start Time</th>
            <th>Total Altitude (m)</th>
            <th>Average Speed (m/s)</th>
            <th>Top Speed (m/s)</th>
          </tr>
        </thead>
        <tbody>
          {runs.map((run) => (
            <tr
              key={run.id}
              onClick={() => handleRowClick(run.id)}
              style={{
                cursor: "pointer",
                height: "50px", // Adjust row height
              }}
            >
              <td>{new Date(run.start_time).toLocaleTimeString()}</td>
              <td>{run.total_altitude.toFixed(2)}</td>
              <td>{run.avg_speed.toFixed(2)}</td>
              <td>{run.top_speed.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default RunSummary;