import React, { useState, useEffect } from 'react';

const AISearchPlugin = () => {
  const [response, setResponse] = useState('');        // AI's response from fetch-logs
  const [oldAPIResponse, setOldAPIResponse] = useState(null); // Response from old-logs
  const [loading, setLoading] = useState(false);       // Loading state for fetchAIResponse
  const [error, setError] = useState(null);            // Error state for fetchAIResponse

  // Fetch AI response when the button is clicked
  const fetchAIResponse = async () => {
    setLoading(true);
    setError(null);

    try {
      const res = await fetch(
        'http://localhost:9000/api/plugins/org.graylog.aisearch/aisearch/fetch-logs',
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );

      if (!res.ok) {
        const errorDetails = await res.json();
        throw new Error(errorDetails.message || 'Failed to fetch AI response');
      }

      const data = await res.json();
      const aiContent =
        data?.choices?.[0]?.message?.content || 'No response content available.';
      setResponse(aiContent);
    } catch (err) {
      setError(err.message || 'An unexpected error occurred');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
     const fetchOldAPI = async () => {
       setLoading(true);
       setError(null);

       try {
         const res = await fetch(
           'http://localhost:9000/api/plugins/org.graylog.aisearch/aisearch/old-logs',
           {
             method: 'GET',
             headers: {
               'Content-Type': 'application/json',
             },
           }
         );

         if (!res.ok) {
           const errorDetails = await res.json();
           throw new Error(errorDetails.message || 'Failed to fetch AI response');
         }

         const data = await res.json();

         // 1) Check if the endpoint returned { message: "Fail" }
         if (data.message === 'Fail') {
           // You can handle "Fail" however you want:
           setOldAPIResponse('The old-logs request returned a FAIL response');
         } else {
           // 2) Otherwise, assume it's a valid OpenAI-like response
           const aiContent =
             data?.choices?.[0]?.message?.content || 'No response content available.';
           setOldAPIResponse(aiContent);
         }
       } catch (err) {
         setError(err.message || 'An unexpected error occurred');
       } finally {
         setLoading(false);
       }
     };

     fetchOldAPI();
   }, []);


  return (
    <div style={{ padding: '20px' }}>
      <h1>AI-Powered Graylog Insights</h1>
      <p>Click the button below to get AI-generated insights.</p>

      <div style={{ marginBottom: '10px' }}>
        <button
          onClick={fetchAIResponse}
          disabled={loading}
          style={{
            backgroundColor: '#0073e6',
            color: 'white',
            border: 'none',
            padding: '10px 20px',
            borderRadius: '5px',
            cursor: 'pointer',
            opacity: loading ? 0.6 : 1,
          }}
        >
          {loading ? 'Loading...' : 'Fetch AI Response'}
        </button>
      </div>

      {/* Show loading indicator if in the process of fetching */}
      {loading && (
        <div style={{ marginTop: '10px', color: '#0073e6' }}>
          Fetching AI response, please wait...
        </div>
      )}

      {/* Show error if one occurred */}
      {error && (
        <div style={{ color: 'red', marginTop: '10px' }}>
          <strong>Error:</strong> {error}
        </div>
      )}

      {/* Render logic:
          1. If AI response is present, show it first (AI has priority).
          2. Else if oldAPIResponse is present, show that.
          3. Else, if not loading or error, show the "No response yet" fallback.
      */}
      {response ? (
        // AI Response
        <div
          style={{
            marginTop: '20px',
            padding: '10px',
            border: '1px solid #ddd',
            borderRadius: '5px',
            whiteSpace: 'pre-wrap',
            wordWrap: 'break-word',
          }}
        >
          <h3>AI's Response:</h3>
          <p>{response}</p>
        </div>
      ) : oldAPIResponse && oldAPIResponse !== 'The old-logs request returned a FAIL response' ? (
        // Old API Response
        <div
          style={{
            marginTop: '20px',
            padding: '10px',
            border: '1px solid #ddd',
            borderRadius: '5px',
            whiteSpace: 'pre-wrap',
            wordWrap: 'break-word',
          }}
        >
          <h3>Old API's Response:</h3>
          <p>{oldAPIResponse}</p>
        </div>
      ) : (
        // Fallback (only shown if there's no AI response AND no old API response,
        // and we're not in the middle of loading or error)
        !loading &&
        !error && (
          <div style={{ marginTop: '10px', color: '#888' }}>
            No AI response yet. Click the button to fetch insights.
          </div>
        )
      )}
    </div>
  );
};

export default AISearchPlugin;
