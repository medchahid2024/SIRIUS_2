import React, { useMemo, useState } from "react";

const IMAGE_EXTENSIONS = [".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".svg"];
const VIDEO_EXTENSIONS = [".mp4", ".webm", ".ogg", ".mov"];

function getCleanMediaUrl(mediaURL) {
  if (!mediaURL || typeof mediaURL !== "string") return null;
  const trimmed = mediaURL.trim();
  return trimmed.length > 0 ? trimmed : null;
}

function inferMediaType(url) {
  if (!url) return "none";

  const lowerUrl = url.toLowerCase().split("?")[0].split("#")[0];

  if (IMAGE_EXTENSIONS.some((ext) => lowerUrl.endsWith(ext))) {
    return "image";
  }

  if (VIDEO_EXTENSIONS.some((ext) => lowerUrl.endsWith(ext))) {
    return "video";
  }

  return "unknown";
}

export default function PublicationMedia({ mediaURL, alt }) {
  const [hasError, setHasError] = useState(false);

  const cleanUrl = useMemo(() => getCleanMediaUrl(mediaURL), [mediaURL]);
  const mediaType = useMemo(() => inferMediaType(cleanUrl), [cleanUrl]);

  if (!cleanUrl || hasError) {
    return null;
  }

  if (mediaType === "image" || mediaType === "unknown") {
    return (
      <div className="publication-media-wrapper">
        <img
          src={cleanUrl}
          alt={alt || "Média de la publication"}
          className="publication-media-image"
          loading="lazy"
          onError={() => setHasError(true)}
        />
      </div>
    );
  }

  if (mediaType === "video") {
    return (
      <div className="publication-media-wrapper">
        <video
          className="publication-media-video"
          controls
          preload="metadata"
          onError={() => setHasError(true)}
        >
          <source src={cleanUrl} />
          Votre navigateur ne supporte pas la lecture vidéo.
        </video>
      </div>
    );
  }

  return null;
}